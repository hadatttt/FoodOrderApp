const WebSocket = require('ws');
const os = require('os');
const db = require('./firebase');

const wss = new WebSocket.Server({ port: 8080, host: '0.0.0.0' });

const storeConnections = new Map(); // storeId -> WebSocket
const userConnections = new Map(); // userId -> WebSocket

wss.on('connection', function connection(ws) {
    console.log('🆕 New client connected');

    ws.on('message', async function incoming(message) {
        const msgStr = message.toString();
        console.log('📥 Received raw message:', msgStr);

        try {
            const data = JSON.parse(msgStr);

            // Register for store
            if (data.type === 'register' && data.storeId) {
                storeConnections.set(data.storeId, ws);
                console.log(`✅ Store ${data.storeId} connected.`);
                ws.send(JSON.stringify({ type: 'registered', storeId: data.storeId }));
            }

            // Register for user
            else if (data.type === 'register_user' && data.userId) {
                userConnections.set(data.userId, ws);
                console.log(`👤 User ${data.userId} connected.`);
                ws.send(JSON.stringify({ type: 'registered_user', userId: data.userId }));
            } else if (data.type === 'cancel_request' && data.orderId) {
                const shopId = data.storeId;

                if (shopId) {
                    const storeWs = storeConnections.get(shopId);

                    const cancelMsg = {
                        type: 'cancel_request',
                        shopId: shopId,
                        orderId: data.orderId,
                        reason: data.reason || '',
                    };

                    if (storeWs && storeWs.readyState === WebSocket.OPEN) {
                        storeWs.send(JSON.stringify(cancelMsg));
                        console.log(`📩 Sent cancel message to store ${shopId}:`, cancelMsg);
                    } else {
                        console.log(`❌ Store ${shopId} not connected`);
                    }
                } else {
                    console.log(`⚠️ Cannot find store for order ${data.orderId}`);
                }
            } else if ((data.type === 'cancelled' || data.type === 'complete' || data.type === 'delivery') && data.userId && data.orderId) {
                const userId = data.userId;

                if (userId) {
                    const userWs = userConnections.get(userId);

                    const msg = {
                        type: data.type,
                        userId: userId,
                        orderId: data.orderId,
                        reason: data.reason || '',
                    };

                    if (userWs && userWs.readyState === WebSocket.OPEN) {
                        userWs.send(JSON.stringify(msg));
                        console.log(`📩 Sent cancel message to user ${userId}:`, msg);
                    } else {
                        console.log(`❌ Store ${shopId} not connected`);
                    }
                } else {
                    console.log(`⚠️ Cannot find store for order ${data.orderId}`);
                }
            } else if (data.type === 'reload_orders' && data.storeId) {
                const shopId = data.storeId;

                if (shopId) {
                    const storeWs = storeConnections.get(shopId);

                    const cancelMsg = {
                        type: 'reload_orders',
                        shopId: shopId || '',
                    };

                    if (storeWs && storeWs.readyState === WebSocket.OPEN) {
                        storeWs.send(JSON.stringify(cancelMsg));
                        console.log(`📩 Sent cancel message to store ${shopId}:`, cancelMsg);
                    } else {
                        console.log(`❌ Store ${shopId} not connected`);
                    }
                } else {
                    console.log(`⚠️ Cannot find store for order ${data.orderId}`);
                }
            }


            // Unknown type
            else {
                console.log('⚠️ Unknown message type or missing fields:', data);
            }

        } catch (e) {
            console.error('❗ Error parsing message:', e);
        }
    });

    ws.on('close', () => {
        // Remove store connection
        for (const [storeId, socket] of storeConnections.entries()) {
            if (socket === ws) {
                storeConnections.delete(storeId);
                console.log(`❌ Store ${storeId} disconnected`);
                break;
            }
        }
        // Remove user connection
        for (const [userId, socket] of userConnections.entries()) {
            if (socket === ws) {
                userConnections.delete(userId);
                console.log(`❌ User ${userId} disconnected`);
                break;
            }
        }
    });
});

// 🔍 Lấy IP LAN để kết nối từ thiết bị thật
function getLocalIP() {
    const interfaces = os.networkInterfaces();
    for (const iface of Object.values(interfaces)) {
        for (const i of iface) {
            if (i.family === 'IPv4' && !i.internal) {
                return i.address;
            }
        }
    }
    return 'localhost';
}

console.log(`🚀 WebSocket server running on ws://${getLocalIP()}:8080`);

// 🔎 Truy vấn Firestore để lấy userId từ orderId
async function getUserIdByOrderId(orderId) {
    try {
        const doc = await db.collection('orders').doc(orderId).get();
        if (doc.exists) {
            return doc.data().userId;
        }
    } catch (e) {
        console.error('❗ Error getting userId from Firestore:', e);
    }
    return null;
}
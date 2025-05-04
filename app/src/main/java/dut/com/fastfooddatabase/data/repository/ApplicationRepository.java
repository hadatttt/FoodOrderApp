package dut.com.fastfooddatabase.data.repository;

public class ApplicationRepository {
    private static ApplicationRepository instance;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;

    private ApplicationRepository() {
        userRepository = new UserRepository();
        shopRepository = new ShopRepository();
        menuItemRepository = new MenuItemRepository();
        orderRepository = new OrderRepository();
        reviewRepository = new ReviewRepository();

    }

    public static synchronized ApplicationRepository getInstance() {
        if (instance == null) {
            instance = new ApplicationRepository();
        }
        return instance;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public ShopRepository getShopRepository() {
        return shopRepository;
    }

    public MenuItemRepository getMenuItemRepository() {
        return menuItemRepository;
    }
}

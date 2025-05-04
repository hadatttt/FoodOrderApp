package dut.com.fastfooddatabase.data.models;

public class SearchHistory {
    private String userId;
    private String keyword;
    private long timestamp;

    public SearchHistory() {}

    public SearchHistory(String userId, String keyword, long timestamp) {
        this.userId = userId;
        this.keyword = keyword;
        this.timestamp = timestamp;
    }

    public String getUserId() { return userId; }
    public String getKeyword() { return keyword; }
    public long getTimestamp() { return timestamp; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

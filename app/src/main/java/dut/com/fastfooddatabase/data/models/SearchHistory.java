package dut.com.fastfooddatabase.data.models;

import java.util.Date;

public class SearchHistory {
    private String userId;
    private String keyword;
    private Date timestamp;

    public SearchHistory() {}

    public SearchHistory(String userId, String keyword, Date timestamp) {
        this.userId = userId;
        this.keyword = keyword;
        this.timestamp = timestamp;
    }

    public String getUserId() { return userId; }
    public String getKeyword() { return keyword; }
    public Date getTimestamp() { return timestamp; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}

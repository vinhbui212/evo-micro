package org.example.thuan_security.request;

public class PagingRequest {
    private int page;
    private int size;

    // Constructor
    public PagingRequest() {
        this.page = 0; // default value
        this.size = 20; // default value
    }

    // Getter and Setter
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}

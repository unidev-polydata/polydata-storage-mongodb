package com.unidev.polydata;


public class PolyQuery {

    protected String tag;
    protected int page = 0;
    protected int itemPerPage = 0;
    private Boolean randomOrder = false;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getItemPerPage() {
        return itemPerPage;
    }

    public void setItemPerPage(int itemPerPage) {
        this.itemPerPage = itemPerPage;
    }

    public Boolean getRandomOrder() {
        return randomOrder;
    }

    public void setRandomOrder(Boolean randomOrder) {
        this.randomOrder = randomOrder;
    }
}

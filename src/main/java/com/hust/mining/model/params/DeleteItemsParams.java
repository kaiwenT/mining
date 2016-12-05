package com.hust.mining.model.params;

public class DeleteItemsParams {
    private int currentSet;
    private int[] indexSet;

    public int getCurrentSet() {
        return currentSet;
    }

    public void setCurrentSet(int currentSet) {
        this.currentSet = currentSet;
    }

    public int[] getIndexSet() {
        return indexSet;
    }

    public void setIndexSet(int[] indexSet) {
        this.indexSet = indexSet;
    }

}

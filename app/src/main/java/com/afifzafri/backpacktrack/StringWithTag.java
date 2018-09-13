package com.afifzafri.backpacktrack;

/**
 * Created by zafri on 10/4/2018.
 * Custom class for ArrayList
 * for storing keys and value
 */

public class StringWithTag {
    public String key;
    public String val;

    public StringWithTag(String key, String val) {
        this.key = key;
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}

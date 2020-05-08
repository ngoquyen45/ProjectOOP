package com.viettel.backend.util.entity;

import java.math.BigDecimal;
import java.util.HashMap;

public class IncrementHashMap<K> extends HashMap<K, BigDecimal> {

    private static final long serialVersionUID = -2843736955958820167L;
    
    public IncrementHashMap() {
        super();
    }
    
    @Override
    public BigDecimal get(Object key) {
        BigDecimal value = super.get(key);
        return value == null ? BigDecimal.ZERO : value;
    }
    
    public double getDoubleValue(Object key) {
        BigDecimal value = super.get(key);
        return value == null ? 0.0 : value.doubleValue();
    }
    
    public int getIntValue(Object key) {
        BigDecimal value = super.get(key);
        return value == null ? 0 : value.intValue();
    }

    public void increment(K key, BigDecimal value) {
        if (value != null) {
            BigDecimal old = super.get(key);
            if (old == null) {
                old = BigDecimal.ZERO;
            }
            
            value = old.add(value);
            super.put(key, value);
        }
    }
    
    public void increment(K key, double value) {
        increment(key, new BigDecimal(value));
    }
    
    public void increment(K key, int value) {
        increment(key, new BigDecimal(value));
    }
    
}

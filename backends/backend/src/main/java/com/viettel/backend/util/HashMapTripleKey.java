package com.viettel.backend.util;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author kimhatrung
 */
public class HashMapTripleKey<K1, K2, K3, V> {

    private HashMap<K1, HashMap<K2, HashMap<K3, V>>> map;

    public HashMapTripleKey() {
        this.map = new HashMap<K1, HashMap<K2, HashMap<K3, V>>>();
    }

    public V get(K1 key1, K2 key2, K3 key3) {
        HashMap<K2, HashMap<K3, V>> map2 = map.get(key1);
        if(map2 != null){
            HashMap<K3,V> map3 = map2.get(key2);
            if(map3 != null){
                return map3.get(key3);
            }else{
                return null;
            }
        }else {
            return null;
        }
    }

    public V put(K1 key1, K2 key2, K3 key3, V value) {
        HashMap<K2, HashMap<K3, V>> map2 = map.get(key1);
        if(map2 == null){
            map2 = new HashMap<K2, HashMap<K3, V>>();
            HashMap<K3,V> map3 = new HashMap<K3, V>();
            map3.put(key3, value);
            map2.put(key2, map3);
            map.put(key1, map2);
            return null;

        }else {
            HashMap<K3,V> map3 = map2.get(key2);
            if(map3 == null){
                map3 = new HashMap<K3, V>();
                map3.put(key3, value);
                map2.put(key2, map3);
                return null;
            } else {
                return map3.put(key3, value);
            }
        }
    }
    
    public boolean isEmpty(){
        return map.isEmpty();
    }
    
    public boolean containsKey(K1 key1, K2 key2, K3 key3){
        HashMap<K2, HashMap<K3, V>> map2 = map.get(key1);
        if(map2 != null){
            HashMap<K3,V> map3 = map2.get(key2);
            if(map3 != null){
                return true;
            }else{
                return false;
            }
        }else {
            return false;
        }
    }
    
    public boolean containsValue(V value){
        for(Entry<K1, HashMap<K2, HashMap<K3, V>>> entry2 : map.entrySet()){
            for(Entry<K2, HashMap<K3,V>> entry3 :entry2.getValue().entrySet()){
                if(entry3.getValue().containsValue(value)){
                    return true;
                }
            }
        }
        
        return false;
    }
}

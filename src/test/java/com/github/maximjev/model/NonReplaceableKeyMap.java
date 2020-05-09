package com.github.maximjev.model;

import java.util.LinkedHashMap;

/**
 * Acts as a map but prevents keys from being overwritten
 */
public class NonReplaceableKeyMap<K, V> extends LinkedHashMap<K, V> {
  @Override
  public V put(K key, V value) {
    if(containsKey(key)) {
      throw new IllegalStateException();
    }
    return super.put(key, value);
  }
}

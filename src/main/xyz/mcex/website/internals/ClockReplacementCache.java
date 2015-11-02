package xyz.mcex.website.internals;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implements WSClock replacement algorithm with a threshold of 2 minutes.
 * @param <K> the key
 * @param <V> the value
 */
public class ClockReplacementCache<K, V>
{
  private int _cacheSize;
  private int _nElements = 0;
  public Map<K, ClockNode> _map = new ConcurrentHashMap<>();
  private ClockNode _clock = null;
  private ClockNode _firstNode = null;
  private static long TIME_THRESHOLD = 120000; // 2 minutes
  private Lock getMtx = new ReentrantLock();

  public ClockReplacementCache(int cacheSize)
  {
    this._cacheSize = cacheSize;
  }

  public synchronized void put(K key, V value)
  {
    final ClockNode node = new ClockNode(key, value);
    if (_nElements < this._cacheSize)
    {
      if (this._firstNode == null)
      {
        this._clock = node;
        this._firstNode = node;
      }

      this._clock.setNext(node);
      this._clock = node;
      node.setNext(this._firstNode);
      this._map.put(key, node);
      ++_nElements;
      return;
    }

    while (this._clock.next.isFresh(TIME_THRESHOLD))
    {
      this._clock.next.reset();
      this._clock = this._clock.next;
    }

    this.getMtx.lock();
    if (this._map.containsKey(this._clock.next.key))
      this._map.remove(this._clock.next.key);
    this.getMtx.unlock();

    node.next = this._clock.next.next;
    this._clock.next = node;
    this._clock = this._clock.next;
    this._map.put(key, node);
  }

  public boolean renew(K key)
  {
    boolean found = false;
    this.getMtx.lock();
    if (this._map.containsKey(key))
    {
      ClockNode node = this._map.get(key);
      node.use();
      found = true;
    }
    this.getMtx.unlock();
    return found;
  }

  public V get(K key)
  {
    ClockNode node = null;
    this.getMtx.lock();
    if (this._map.containsKey(key))
    {
      node = this._map.get(key);
      node.use();
    }
    this.getMtx.unlock();

    if (node == null)
      return null;
    return node.value;
  }

  private class ClockNode
  {
    public K key;
    public V value;
    public long lastUsed;
    public boolean fresh = true;
    public ClockNode next;

    public ClockNode(K key, V value)
    {
      this.value = value;
      this.key = key;
      this.lastUsed = System.currentTimeMillis();
    }

    public void reset()
    {
      this.fresh = false;
    }

    public void use()
    {
      this.fresh = true;
      this.lastUsed = System.currentTimeMillis();
    }

    public boolean isFresh(long threshold)
    {
      return ((System.currentTimeMillis() - this.lastUsed) <= threshold) && this.fresh;
    }

    public void setNext(ClockNode node)
    {
      this.next = node;
    }
  }
}

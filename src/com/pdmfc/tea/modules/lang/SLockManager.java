/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * SLockManager.java
 *
 * 2000/12
 *
 *
 * Revisions:
 *
 * 2000/12/03
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.lang;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;





/**************************************************************************
 *
 * Used to manage locks. After a thread acquires a lock, when other
 * threads try to acquire that same lock they will block until the
 * first thread releases the lock. Instances of this class are used to
 * support exclusive access to resources.
 *
 **************************************************************************/

class SLockManager
    extends Object {


    // All the locks created so far.
    private Hashtable _allLocks;

    // Locks that have not been acquired.
    private Hashtable _unusedLocks;

    // Locks that have been acquired. Value is owner of lock.
    private Hashtable _usedLocks;





/**************************************************************************
 *
 * Initializes the object.
 *
 **************************************************************************/

    public SLockManager() {

	_allLocks    = new Hashtable();
	_unusedLocks = new Hashtable();
	_usedLocks   = new Hashtable();
    }





/**************************************************************************
 *
 * Acquires a lock. If the lock had already been acquired by a
 * previous call to <code>acquireLock</code> and not yet released by
 * calling <code>releaseLock()</code> then the invoking thread will
 * block until that particular lock is released.
 *
 * <p>Note that if the same thread calls <code>acquireLock()</code> in
 * succession without an intervening call to
 * <code>releaseLock()</code> then it will deadlock.</p>
 *
 * <p>The first time this method is called for a particular lock that
 * lock is created internally.</p>
 *
 * @param lock
 *	Object being used as lock identifier.
 *
 **************************************************************************/

    public synchronized void acquireLock(Object lock,
					 Object owner)
	throws InterruptedException {

	if ( !lockExists(lock) ) {
	    createLock(lock);
	}
	while ( getLock(lock, owner) == null ) {
	    wait();
	}
    }





/**************************************************************************
 *
 * Releases a previously acquired lock. One of the other threads that
 * are blocked in a call to the <code>acquireLock()</code> method for
 * this particular lock will then resume execution having successfully
 * acquired this lock.
 *
 * <p>Note that if the <code>lock</code> argument specifies an
 * unexisting lock or if that lock is not currently in use then a
 * runtime exception will occur.</p>
 *
 * @param lock
 *	Object being used as lock identifier.
 *
 * @exception java.lang.IllegalArgumentException
 *	Thrown if the specified lock does not exist or if it was
 *	not in use.
 *
 **************************************************************************/

    public synchronized void releaseLock(Object lock) {

	if ( !lockExists(lock) ) {
	    throw new IllegalArgumentException("unknown lock (" + lock + ")");
	}
	if ( !lockIsInUse(lock) ) {
	    throw new IllegalArgumentException("lock '" + lock
					       + "' is not in use");
	}
	addLock(lock);
	notifyAll();
    }





/**************************************************************************
 *
 * Releases all the currently acquired locks by the specified owner.
 *
 **************************************************************************/

    public synchronized void releaseAllLocks(Object owner) {

	Vector      inUse = new Vector();
	Enumeration locks = _usedLocks.elements();

	// Copies identifiers of locks in use to the vector.
	while ( locks.hasMoreElements() ) {
	    Object lock = locks.nextElement();
	    if ( _usedLocks.get(lock) == owner ) {
		inUse.addElement(locks.nextElement());
	    }
	}

	locks = inUse.elements();
	while ( locks.hasMoreElements() ) {
	    addLock(locks.nextElement());
	}
	notifyAll();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private synchronized boolean lockExists(Object lock) {

	return _allLocks.containsKey(lock);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private synchronized boolean lockIsInUse(Object lock) {

	return _usedLocks.containsKey(lock);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private synchronized void createLock(Object newLock) {

	_allLocks.put(newLock, newLock);
	_unusedLocks.put(newLock, newLock);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private synchronized Object getLock(Object lock,
					Object owner) {

	Object result = _unusedLocks.remove(lock);
	if ( result != null ) {
	    _usedLocks.put(lock, owner);
	}

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private synchronized void addLock(Object lock) {

	_unusedLocks.put(lock, lock);
	_usedLocks.remove(lock);
    }
}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/


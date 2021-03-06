/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.proxy.item;

import org.sonatype.nexus.proxy.access.Action;

/**
 * Repository item UID lock represents a global lock that is able to sync across multiple threads.
 * 
 * @author cstamas
 */
public interface RepositoryItemUidLock
{
    /**
     * Locks this UID for a given action. Will perform lock upgrade is needed (read -> write). Lock upgrade (ie. create
     * action locking happens after read action already locked) happens, but does not happen atomically! Lock upgrade is
     * actually release all (if any) read lock and then acquire write lock. Once you have exclusive lock, then only you
     * can be sure for unique access.
     * 
     * @param action
     */
    void lock( Action action );

    /**
     * Unlocks UID. It is the responsibility of caller to use lock/unlock properly (ie. boxing of calls).
     */
    void unlock();

    /**
     * Returns true if current thread has locks already on this UID lock.
     * 
     * @return
     */
    boolean hasLocksHeld();
}

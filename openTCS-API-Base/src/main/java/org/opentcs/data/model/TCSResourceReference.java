/*
 * openTCS copyright information:
 * Copyright (c) 2006 Fraunhofer IML
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.data.model;

import java.io.Serializable;
import org.opentcs.data.TCSObjectReference;

/**
 * A transient reference to a <code>TCSResource</code>.
 *
 * @author Stefan Walter (Fraunhofer IML)
 * @param <E> The actual resource class.
 */
public class TCSResourceReference<E extends TCSResource<E>>
extends TCSObjectReference<E>
implements Serializable, Cloneable {
  /**
   * Creates a new TCSResourceReference.
   *
   * @param newReferent The resource this reference references.
   */
  protected TCSResourceReference(TCSResource<E> newReferent) {
    super(newReferent);
  }
  
  @Override
  public TCSResourceReference<E> clone() {
    TCSResourceReference<E> clone = (TCSResourceReference<E>) super.clone();
    return clone;
  }
}

/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
/*
 *
 * Created on June 8, 2006, 9:15 AM
 */
package org.opentcs.kernel.workingset;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.listener.Handler;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.opentcs.data.ObjectExistsException;
import org.opentcs.data.TCSObjectEvent;
import org.opentcs.data.model.Path;
import org.opentcs.data.model.Point;
import org.opentcs.util.eventsystem.TCSEvent;

/**
 * A test class for TCSObjectPool.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class TCSObjectPoolTest {

  /**
   * The pool to be tested here.
   */
  private TCSObjectPool pool;

  @Before
  public void setUp() {
    pool = new TCSObjectPool(new MBassador<>(BusConfiguration.Default()));
  }

  @After
  public void tearDown() {
    pool = null;
  }

  @Test
  public void shouldReturnObjectByClassAndName() {
    Point point1 = new Point(pool.getUniqueObjectId(), "Point-00001");
    pool.addObject(point1);
    Point point2 = new Point(pool.getUniqueObjectId(), "Point-00002");
    pool.addObject(point2);

    assertEquals(point1, pool.getObject(Point.class, "Point-00001"));
    assertEquals(point2, pool.getObject(Point.class, "Point-00002"));
  }

  @Test
  public void shouldReturnObjectByName() {
    Point point1 = new Point(pool.getUniqueObjectId(), "Point-00001");
    pool.addObject(point1);
    Point point2 = new Point(pool.getUniqueObjectId(), "Point-00002");
    pool.addObject(point2);

    assertEquals(point1, pool.getObject("Point-00001"));
    assertEquals(point2, pool.getObject("Point-00002"));
  }

  @Test
  public void shouldReturnObjectByClassAndRef() {
    Point point1 = new Point(pool.getUniqueObjectId(), "Point-00001");
    pool.addObject(point1);
    Point point2 = new Point(pool.getUniqueObjectId(), "Point-00002");
    pool.addObject(point2);

    assertEquals(point1, pool.getObject(Point.class, point1.getReference()));
    assertEquals(point2, pool.getObject(Point.class, point2.getReference()));
  }

  @Test
  public void shouldReturnObjectByRef() {
    Point point1 = new Point(pool.getUniqueObjectId(), "Point-00001");
    pool.addObject(point1);
    Point point2 = new Point(pool.getUniqueObjectId(), "Point-00002");
    pool.addObject(point2);

    assertEquals(point1, pool.getObject(point1.getReference()));
    assertEquals(point2, pool.getObject(point2.getReference()));
  }

  @Test
  public void shouldReturnObjectsByClassAndPattern() {
    Point point1 = new Point(pool.getUniqueObjectId(), "Point-00001");
    pool.addObject(point1);
    Point point2 = new Point(pool.getUniqueObjectId(), "Point-00002");
    pool.addObject(point2);
    Path path1 = new Path(pool.getUniqueObjectId(), "Point-00003",
                          point1.getReference(), point2.getReference());
    pool.addObject(path1);

    Set<Point> points = pool.getObjects(Point.class, Pattern.compile("Point.*"));

    Iterator<Point> it = points.iterator();
    assertEquals(2, points.size());
    assertEquals(point1, it.next());
    assertEquals(point2, it.next());
  }

  @Test
  public void shouldReturnObjectsByClass() {
    Point point1 = new Point(pool.getUniqueObjectId(), "Point-00001");
    pool.addObject(point1);
    Point point2 = new Point(pool.getUniqueObjectId(), "Point-00002");
    pool.addObject(point2);
    Path path1 = new Path(pool.getUniqueObjectId(), "Path-00001",
                          point1.getReference(), point2.getReference());
    pool.addObject(path1);

    Set<Point> points = pool.getObjects(Point.class);

    Iterator<Point> it = points.iterator();
    assertEquals(2, points.size());
    assertEquals(point1, it.next());
    assertEquals(point2, it.next());
  }

  @Test
  public void shouldReturnObjectsByPattern() {
    Point point1 = new Point(pool.getUniqueObjectId(), "Point-00001");
    pool.addObject(point1);
    Point point2 = new Point(pool.getUniqueObjectId(), "Point-00002");
    pool.addObject(point2);
    Point point3 = new Point(pool.getUniqueObjectId(), "Punkt-00003");
    pool.addObject(point3);

    Set<Point> points = pool.getObjects(Point.class, Pattern.compile("Point.*"));

    Iterator<Point> it = points.iterator();
    assertEquals(2, points.size());
    assertEquals(point1, it.next());
    assertEquals(point2, it.next());
  }

  @Test
  public void shouldRemoveObjectByRef() {
    Point point1 = new Point(pool.getUniqueObjectId(), "Point-00001");
    pool.addObject(point1);
    assertEquals(point1, pool.getObject("Point-00001"));
    pool.removeObject(point1.getReference());
    assertNull(pool.getObject("Point-00001"));
  }

  @Test
  public void shouldRemoveObjectsByName() {
    Point point1 = new Point(pool.getUniqueObjectId(), "Point-00001");
    pool.addObject(point1);
    Point point2 = new Point(pool.getUniqueObjectId(), "Point-00002");
    pool.addObject(point2);
    assertEquals(point1, pool.getObject(point1.getReference()));
    assertEquals(point2, pool.getObject(point2.getReference()));

    Set<String> names = new HashSet<>();
    names.add("Point-00001");
    names.add("Point-00002");
    pool.removeObjects(names);

    assertNull(pool.getObject("Point-00001"));
    assertNull(pool.getObject("Point-00002"));
  }

  @Test
  public void shouldEmitEventForCreatedObject() {
    MBassador<Object> eventBus = new MBassador<>(BusConfiguration.Default());

    List<TCSEvent> receivedEvents = new LinkedList<>();
    Object eventHandler = new Object() {
      @Handler
      public void handleEvent(TCSEvent event) {
        receivedEvents.add(event);
      }
    };
    eventBus.subscribe(eventHandler);
    pool = new TCSObjectPool(eventBus);
    Point point1 = new Point(pool.getUniqueObjectId(), "Point-00001");
    pool.addObject(point1);

    assertEquals(0, receivedEvents.size());
    pool.emitObjectEvent(point1, point1, TCSObjectEvent.Type.OBJECT_CREATED);
    assertEquals(1, receivedEvents.size());
  }

  @Test(expected = ObjectExistsException.class)
  public void shouldThrowIfAddingExistingName() {
    // A few initial objects
    Point point1 = new Point(pool.getUniqueObjectId(), "Point-00001");
    pool.addObject(point1);
    Point point2 = new Point(pool.getUniqueObjectId(), "Point-00002");
    pool.addObject(point2);
    Path path1 = new Path(pool.getUniqueObjectId(), "Path-00001",
                          point1.getReference(), point2.getReference());
    pool.addObject(path1);
    Path path2 = new Path(pool.getUniqueObjectId(), "Path-00002",
                          point2.getReference(), point1.getReference());
    pool.addObject(path2);
    // A misnamed/duplicate object
    pool.addObject(new Point(pool.getUniqueObjectId(), "Path-00002"));
  }

  /**
   * Verify that the pool generates unique object names.
   */
  @Test(expected = ObjectExistsException.class)
  public void testUniqueNameGenerator() {
    String prefix = "ABC";
    String suffixPattern = "000";
    for (int i = 1; i <= 100; i++) {
      String curName = pool.getUniqueObjectName(prefix, suffixPattern);
      pool.addObject(new Point(pool.getUniqueObjectId(), curName));
    }
    // Add a name that should already exist in the pool, and expect an exception.
    pool.addObject(new Point(pool.getUniqueObjectId(), "ABC050"));
  }
}

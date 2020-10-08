/*
 * openTCS copyright information:
 * Copyright (c) 2007 Fraunhofer IML
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.drivers.vehicle;

import java.util.Map;
import static java.util.Objects.requireNonNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.opentcs.data.model.Location;
import org.opentcs.data.model.Point;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.Route.Step;

/**
 * A command for moving a step.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class MovementCommand {

  /**
   * A constant indicating there is no operation to be executed after moving.
   */
  public static final String NO_OPERATION = DriveOrder.Destination.OP_NOP;
  /**
   * A constant indicating the vehicle should basically just move to a point
   * without a location associated to it.
   */
  public static final String MOVE_OPERATION = DriveOrder.Destination.OP_MOVE;
  /**
   * A constant for parking the vehicle. (Again, basically doing nothing at the
   * destination.)
   */
  public static final String PARK_OPERATION = DriveOrder.Destination.OP_PARK;
  /**
   * The step describing the movement.
   */
  private final Step step;
  /**
   * The operation to be executed after moving.
   */
  private final String operation;
  /**
   * The location at which the operation is to be executed. (May be
   * <code>null</code> if <em>operation</em> is <code>NO_OPERATION</code>.)
   */
  private final Location opLocation;
  /**
   * Indicates whether this movement is the final one for the drive order it belongs to.
   */
  private final boolean finalMovement;
  /**
   * The destination position of the whole drive order.
   */
  private final Point finalDestination;
  /**
   * The operation to be executed at the destination position.
   */
  private final String finalOperation;
  /**
   * Properties of the order this command is part of.
   */
  private final Map<String, String> properties;

  /**
   * Creates a new MovementCommand.
   *
   * @param newStep The step describing the movement.
   * @param newOperation The operation to be executed after moving.
   * @param newOpLocation The location at which the operation is to be executed.
   * May be <code>null</code> if <em>newOperation</em> is
   * <code>NO_OPERATION</code>.)
   * @param finalMovement Indicates whether this movement is the final one in the drive order it
   * belongs to.
   * @param newDestination The destination position of the whole drive order.
   * @param newDestOperation The operation to be executed at the destination
   * position.
   * @param newProperties Properties of the order this command is part of.
   */
  public MovementCommand(Step newStep,
                         String newOperation,
                         Location newOpLocation,
                         boolean finalMovement,
                         Point newDestination,
                         String newDestOperation,
                         Map<String, String> newProperties) {
    this.step = requireNonNull(newStep, "newStep is null");
    this.operation = requireNonNull(newOperation, "newOperation is null");
    this.finalMovement = finalMovement;
    this.finalDestination = requireNonNull(newDestination, "newDestination is null");
    this.finalOperation = requireNonNull(newDestOperation, "newDestOperation is null");
    this.properties = requireNonNull(newProperties, "newProperties is null");
    if (newOpLocation == null && !isEmptyOperation(newOperation)) {
      throw new NullPointerException("newOpLocation is null");
    }
    this.opLocation = newOpLocation;
  }

  /**
   * Returns the step describing the movement.
   *
   * @return The step describing the movement.
   */
  @Nonnull
  public Step getStep() {
    return step;
  }

  /**
   * Returns the operation to be executed after moving.
   *
   * @return The operation to be executed after moving.
   */
  @Nonnull
  public String getOperation() {
    return operation;
  }

  /**
   * Checks whether an operation is to be executed in addition to moving or not.
   *
   * @return <code>true</code> if, and only if, no operation is to be executed.
   */
  public boolean isWithoutOperation() {
    return isEmptyOperation(operation);
  }

  /**
   * Returns the location at which the operation is to be executed. (May be
   * <code>null</code> if <em>operation</em> is <code>NO_OPERATION</code>.)
   *
   * @return The location at which the operation is to be executed.
   */
  @Nullable
  public Location getOpLocation() {
    return opLocation;
  }

  /**
   * Indicates whether this movement is the final one in the driver order it belongs to.
   *
   * @return <code>true</code> if, and only if, this movement is the final one.
   */
  public boolean isFinalMovement() {
    return finalMovement;
  }

  /**
   * Returns the final destination of the drive order this MovementCommand was
   * created for.
   *
   * @return The final destination of the drive order this MovementCommand was
   * created for.
   */
  @Nonnull
  public Point getFinalDestination() {
    return finalDestination;
  }

  /**
   * Returns the operation to be executed at the <em>final</em> destination
   * position.
   *
   * @return The operation to be executed at the <em>final</em> destination
   * position.
   */
  @Nonnull
  public String getFinalOperation() {
    return finalOperation;
  }

  /**
   * Returns the properties of the order this command is part of.
   *
   * @return The properties of the order this command is part of.
   */
  @Nonnull
  public Map<String, String> getProperties() {
    return properties;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof MovementCommand) {
      MovementCommand other = (MovementCommand) o;
      return step.equals(other.step) && operation.equals(other.operation);
    }
    else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return step.hashCode() ^ operation.hashCode();
  }

  @Override
  public String toString() {
    return "MovementCommand{"
        + "step=" + step
        + ", operation=" + operation
        + ", opLocation=" + opLocation
        + ", finalMovement=" + finalMovement
        + ", finalDestination=" + finalDestination
        + ", finalOperation=" + finalOperation
        + ", properties=" + properties
        + '}';
  }

  /**
   * Checks whether an operation means something is to be done in addition to
   * moving or not.
   *
   * @param operation The operation to be checked.
   * @return <code>true</code> if, and only if, the vehicle should only move
   * with the given operation.
   */
  private static boolean isEmptyOperation(String operation) {
    return NO_OPERATION.equals(operation)
        || MOVE_OPERATION.equals(operation)
        || PARK_OPERATION.equals(operation);
  }
}

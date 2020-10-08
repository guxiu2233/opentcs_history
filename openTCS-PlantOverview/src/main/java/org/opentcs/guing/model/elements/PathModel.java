/*
 * openTCS copyright information:
 * Copyright (c) 2005-2011 ifak e.V.
 * Copyright (c) 2012 Fraunhofer IML
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */

package org.opentcs.guing.model.elements;

import java.util.Arrays;
import static java.util.Objects.requireNonNull;
import org.opentcs.data.model.visualization.ElementPropKeys;
import org.opentcs.guing.components.properties.type.BooleanProperty;
import org.opentcs.guing.components.properties.type.IntegerProperty;
import org.opentcs.guing.components.properties.type.KeyValueSetProperty;
import org.opentcs.guing.components.properties.type.LengthProperty;
import org.opentcs.guing.components.properties.type.SelectionProperty;
import org.opentcs.guing.components.properties.type.SpeedProperty;
import org.opentcs.guing.components.properties.type.StringProperty;
import org.opentcs.guing.util.ResourceBundleUtil;

/**
 * A connection between two points.
 * 
 * @author Sebastian Naumann (ifak e.V. Magdeburg)
 */
public class PathModel
    extends AbstractConnection {

  /**
   * Key for the length.
   */
  public static final String LENGTH = "length";
  /**
   * Key for routing costs.
   */
  public static final String ROUTING_COST = "cost";
  /**
   * Key for maximum forward velocity.
   */
  public static final String MAX_VELOCITY = "maxVelocity";
  /**
   * Key for maximum reverse velocity.
   */
  public static final String MAX_REVERSE_VELOCITY = "maxReverseVelocity";
  /**
   * Key for the locked state.
   */
  public static final String LOCKED = "locked";

  /**
   * Creates a new instance.
   */
  public PathModel() {
    createProperties();
  }

  @Override
  public String getDescription() {
    return ResourceBundleUtil.getBundle().getString("path.description");
  }

  private void createProperties() {
    ResourceBundleUtil bundle = ResourceBundleUtil.getBundle();
    // Name
    StringProperty pName = new StringProperty(this);
    pName.setDescription(bundle.getString("path.name.text"));
    pName.setHelptext(bundle.getString("path.name.helptext"));
    setProperty(NAME, pName);
    // Length
    LengthProperty pLength = new LengthProperty(this);
    pLength.setDescription(bundle.getString("path.length.text"));
    pLength.setHelptext(bundle.getString("path.length.helptext"));
    setProperty(LENGTH, pLength);
    // Routing cost
    IntegerProperty pCost = new IntegerProperty(this, 1);
    pCost.setDescription(bundle.getString("path.routingCost.text"));
    pCost.setHelptext(bundle.getString("path.routingCost.helptext"));
    pCost.setCollectiveEditable(true);
    setProperty(ROUTING_COST, pCost);
    // Max. velocity (forward)
    SpeedProperty pMaxVelocity = new SpeedProperty(this, 1.0, SpeedProperty.Unit.M_S);
    pMaxVelocity.setDescription(bundle.getString("path.maxVelocity.text"));
    pMaxVelocity.setHelptext(bundle.getString("path.maxVelocity.helptext"));
    pMaxVelocity.setCollectiveEditable(true);
    setProperty(MAX_VELOCITY, pMaxVelocity);
    // Max. velocity (reverse)
    SpeedProperty pMaxReverseVelocity = new SpeedProperty(this, 0.0, SpeedProperty.Unit.M_S);
    pMaxReverseVelocity.setDescription(bundle.getString("path.maxReverseVelocity.text"));
    pMaxReverseVelocity.setHelptext(bundle.getString("path.maxReverseVelocity.helptext"));
    pMaxReverseVelocity.setCollectiveEditable(true);
    setProperty(MAX_REVERSE_VELOCITY, pMaxReverseVelocity);
    // Type of path coonection
    SelectionProperty<LinerType> pPathConnType = new SelectionProperty<>(this, Arrays.asList(LinerType.values()), LinerType.values()[0]);
    pPathConnType.setDescription(bundle.getString("element.pathConnType.text"));
    pPathConnType.setHelptext(bundle.getString("element.pathConnType.helptext"));
    pPathConnType.setCollectiveEditable(true);
    setProperty(ElementPropKeys.PATH_CONN_TYPE, pPathConnType);
    // Control points (for BEZIER paths)
    StringProperty pPathControlPoints = new StringProperty(this);
    pPathControlPoints.setDescription(bundle.getString("element.pathControlPoints.text"));
    pPathControlPoints.setHelptext(bundle.getString("element.pathControlPoints.helptext"));
    // Control Points können nur in der Drawing verschoben werden.
    // TODO: Auch in der Tabelle editieren?
    pPathControlPoints.setModellingEditable(false);
    setProperty(ElementPropKeys.PATH_CONTROL_POINTS, pPathControlPoints);
    // Components
    StringProperty startComponent = new StringProperty(this);
    startComponent.setDescription(bundle.getString("element.startComponent.text"));
    startComponent.setModellingEditable(false);
    startComponent.setOperatingEditable(false);
    setProperty(START_COMPONENT, startComponent);
    StringProperty endComponent = new StringProperty(this);
    endComponent.setDescription(bundle.getString("element.endComponent.text"));
    endComponent.setModellingEditable(false);
    endComponent.setOperatingEditable(false);
    setProperty(END_COMPONENT, endComponent);
    // Arrow position (relative)
    // HH 2014-02-14: Verschieben der Pfeilspitze ist in der Figure noch nicht
    // implementiert, daher dieses Property auch nicht anzeigen
//    PercentProperty pPathArrowPosition = new PercentProperty();
//    pPathArrowPosition.setDescription(bundle.getString("element.pathArrowPosition.text"));
//    pPathArrowPosition.setHelptext(bundle.getString("element.pathArrowPosition.helptext"));
//    pPathArrowPosition.setCollectiveEditable(true);
//    setPropertyBag(ElementPropKeys.PATH_ARROW_POSITION, new PropertyBag(pPathArrowPosition, this));
    // "Locked" state - also editable in Operating mode
    BooleanProperty pLocked = new BooleanProperty(this);
    pLocked.setDescription(bundle.getString("path.locked.text"));
    pLocked.setHelptext(bundle.getString("path.locked.helptext"));
    pLocked.setCollectiveEditable(true);
    // Zustand "gesperrt" darf auch im Operating Mode geändert werden.
    pLocked.setOperatingEditable(true);
    setProperty(LOCKED, pLocked);
    // Miscellaneous properties
    KeyValueSetProperty pMiscellaneous = new KeyValueSetProperty(this);
    pMiscellaneous.setDescription(bundle.getString("path.miscellaneous.text"));
    pMiscellaneous.setHelptext(bundle.getString("path.miscellaneous.helptext"));
    // HH 2014-02-17: Miscellaneous Properties vorerst nicht collective editable
    // pMiscellaneous.setCollectiveEditable(true);
    setProperty(MISCELLANEOUS, pMiscellaneous);
  }

  /**
   * The supported liner types for connections.
   */
  public enum LinerType {

    /**
     * A direct connection.
     */
    DIRECT,
    /**
     * An elbow connection.
     */
    ELBOW,
    /**
     * A slanted connection.
     */
    SLANTED,
    /**
     * A bezier curve.
     */
    BEZIER;

    /**
     * Returns the <code>LinerType</code> constant matching the name in the
     * given input. This method permits extraneous whitespace around the name
     * and is case insensitive, which makes it a bit more liberal than the
     * default <code>valueOf</code> that all enums provide.
     *
     * @param input The name of the enum constant to return.
     * @return The enum constant matching the given name.
     * @throws IllegalArgumentException If this enum has no constant with the
     * given name.
     */
    public static LinerType valueOfNormalized(String input)
        throws IllegalArgumentException {
      String normalizedInput = requireNonNull(input, "input is null").trim();

      for (LinerType curType : values()) {
        if (normalizedInput.equalsIgnoreCase(curType.name())) {
          return curType;
        }
      }

      throw new IllegalArgumentException("No match for '" + input + "'");
    }

    @Override
    public String toString() {
      return ResourceBundleUtil.getBundle().getString("path.type." + name() + ".text");
    }
  }
}

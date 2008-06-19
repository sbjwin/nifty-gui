package de.lessvoid.nifty.loader.xpp3.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.NiftyInputControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.layout.align.HorizontalAlign;
import de.lessvoid.nifty.layout.align.VerticalAlign;
import de.lessvoid.nifty.loader.xpp3.Attributes;
import de.lessvoid.nifty.loader.xpp3.elements.helper.StyleHandler;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.render.NiftyImageMode;
import de.lessvoid.nifty.render.NiftyRenderEngine;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.nifty.tools.TimeProvider;

/**
 * ElementType.
 * @author void
 */
public class ElementType {
  /**
   * logger.
   */
  private static Logger log = Logger.getLogger(ElementType.class.getName());

  /**
   * attributes.
   */
  private AttributesType attributes;

  /**
   * interact.
   * @optional
   */
  private InteractType interact;

  /**
   * hover.
   * @optional
   */
  private HoverType hover;

  /**
   * EffectsType.
   * @optional
   */
  private EffectsType effects;

  /**
   * elements.
   * @optional
   */
  private Collection < ElementType > elements = new ArrayList < ElementType >();

  /**
   * Create element.
   * @param parent parent element
   * @param nifty nifty
   * @param screen screen
   * @param registeredEffects registeredEffects
   * @param registeredControls registeredControls
   * @param styleHandler style handler
   * @param time time
   * @param inputControl inputControl we should attach to the element (can be null)
   * @param screenController ScreenController
   * @return element
   */
  public Element createElement(
      final Element parent,
      final Nifty nifty,
      final Screen screen,
      final Map < String, RegisterEffectType > registeredEffects,
      final Map < String, RegisterControlDefinitionType > registeredControls,
      final StyleHandler styleHandler,
      final TimeProvider time,
      final NiftyInputControl inputControl,
      final ScreenController screenController) {
    return null;
  }

  /**
   * add attributes to the element.
   * @param element element
   * @param screen screen
   * @param nifty nifty
   * @param registeredEffects effects
   * @param registeredControls registeredControls
   * @param styleHandler style handler
   * @param time time
   * @param control attached control (might be null)
   * @param screenController screenController
   */
  protected void addElementAttributes(
      final Element element,
      final Screen screen,
      final Nifty nifty,
      final Map < String, RegisterEffectType > registeredEffects,
      final Map < String, RegisterControlDefinitionType > registeredControls,
      final StyleHandler styleHandler,
      final TimeProvider time,
      final NiftyInputControl control,
      final ScreenController screenController) {
    element.bindToScreen(nifty);

    // if the element we process has a style set, we try to apply
    // the style attributes first
    String styleId = attributes.getStyle();
    applyStyle(element, nifty, registeredEffects, styleHandler, time, styleId);

    // now apply our own attributes
    applyAttributes(attributes, element, nifty.getRenderDevice());

    // interact
    if (interact != null) {
      // control given?
      if (control != null) {
        interact.initWithControl(element, control, screenController);
      } else {
        interact.initWithScreenController(element, screenController);
      }
    }
    // hover
    if (hover != null) {
      hover.initElement(element);
    }
    // effects
    if (effects != null) {
      effects.initElement(element, nifty, registeredEffects, time);
    }
    // children
    for (ElementType elementType : elements) {
      elementType.createElement(
          element,
          nifty,
          screen,
          registeredEffects,
          registeredControls,
          styleHandler,
          time,
          control,
          screenController);
    }
  }

  /**
   * apply style.
   * @param element element
   * @param nifty nifty
   * @param registeredEffects effects
   * @param styleHandler style handler
   * @param time time provider
   * @param styleId style id
   */
  private void applyStyle(
      final Element element,
      final Nifty nifty,
      final Map < String, RegisterEffectType > registeredEffects,
      final StyleHandler styleHandler,
      final TimeProvider time,
      final String styleId) {
    if (styleId != null) {
      StyleType style = styleHandler.getStyle(styleId);
      if (style != null) {
        style.applyStyle(element, nifty, registeredEffects, time);
      }
    }
  }

  /**
   * apply given attributes to the element.
   * @param attrib attributes
   * @param element the element to apply attributes
   * @param renderDevice RenderDevice
   */
  public static void applyAttributes(
      final AttributesType attrib,
      final Element element,
      final NiftyRenderEngine renderDevice) {
    if (attrib == null) {
      return;
    }
    // height
    if (attrib.getHeight() != null) {
      SizeValue heightValue = new SizeValue(attrib.getHeight());
      element.setConstraintHeight(heightValue);
    }
    // width
    if (attrib.getWidth() != null) {
      SizeValue widthValue = new SizeValue(attrib.getWidth());
      element.setConstraintWidth(widthValue);
    }
    // set absolute x position when given
    if (attrib.getX() != null) {
      element.setConstraintX(new SizeValue(attrib.getX()));
    }
    // set absolute y position when given
    if (attrib.getY() != null) {
      element.setConstraintY(new SizeValue(attrib.getY()));
    }
    // horizontal align
    if (attrib.getAlign() != null) {
      element.setConstraintHorizontalAlign(HorizontalAlign.valueOf(attrib.getAlign().getValue()));
    }
    // vertical align
    if (attrib.getValign() != null) {
      element.setConstraintVerticalAlign(VerticalAlign.valueOf(attrib.getValign().getValue()));
    }
    // child clip
    if (attrib.getChildClip() != null) {
      element.setClipChildren(attrib.getChildClip());
    }
    // visible
    if (attrib.getVisible() != null) {
      if (attrib.getVisible()) {
        element.show();
      } else {
        element.hide();
      }
    }
    // visibleToMouse
    if (attrib.getVisibleToMouse() != null) {
      element.setVisibleToMouseEvents(attrib.getVisibleToMouse());
    }
    // childLayout
    if (attrib.getChildLayoutType() != null) {
      element.setLayoutManager(attrib.getChildLayoutType().getLayoutManager());
    }
    // textRenderer
    TextRenderer textRenderer = element.getRenderer(TextRenderer.class);
    if (textRenderer != null) {
      // font
      if (attrib.getFont() != null) {
        textRenderer.setFont(renderDevice.createFont(attrib.getFont()));
      }
      // text horizontal align
      if (attrib.getTextHAlign() != null) {
        textRenderer.setTextHAlign(HorizontalAlign.valueOf(attrib.getTextHAlign().getValue()));
      }
      // text vertical align
      if (attrib.getTextVAlign() != null) {
        textRenderer.setTextVAlign(VerticalAlign.valueOf(attrib.getTextVAlign().getValue()));
      }
      // text color
      if (attrib.getColor() != null) {
        textRenderer.setColor(attrib.getColor().createColor());
      }
      // text
      if (attrib.getText() != null) {
        textRenderer.setText(attrib.getText());
//        element.setConstraintHeight(new SizeValue(textRenderer.getTextHeight() + "px"));
//        element.setConstraintWidth(new SizeValue(textRenderer.getTextWidth() + "px"));
      }
    }
    // panelRenderer
    PanelRenderer panelRenderer = element.getRenderer(PanelRenderer.class);
    if (panelRenderer != null) {
      // background color
      if (attrib.getBackgroundColor() != null) {
        panelRenderer.setBackgroundColor(attrib.getBackgroundColor().createColor());
      }
    }
    // imageRenderer
    ImageRenderer imageRenderer = element.getRenderer(ImageRenderer.class);
    if (imageRenderer != null) {
      // filename
      if (attrib.getFilename() != null) {
        imageRenderer.setImage(renderDevice.createImage(attrib.getFilename(), attrib.getFilter()));
      }
      if (attrib.getBackgroundImage() != null) {
        imageRenderer.setImage(renderDevice.createImage(attrib.getBackgroundImage(), attrib.getFilter()));
      }
      // set imageMode?
      NiftyImage image = imageRenderer.getImage();
      String imageMode = attrib.getImageMode();
      if (image != null && imageMode != null) {
          image.setImageMode(NiftyImageMode.valueOf(imageMode));
      }
    }
  }

  /**
   * add element.
   * @param elementType elementType
   */
  public void addElementType(final ElementType elementType) {
    elements.add(elementType);
  }

  /**
   * set interact.
   * @param interactParam interact
   */
  public void setInteract(final InteractType interactParam) {
    this.interact = interactParam;
  }

  /**
   * set hover.
   * @param hoverParam hover
   */
  public void setHover(final HoverType hoverParam) {
    this.hover = hoverParam;
  }

  /**
   * set effects.
   * @param effectsParam effects
   */
  public void setEffects(final EffectsType effectsParam) {
    this.effects = effectsParam;
  }

  /**
   * get attributes.
   * @return attributes
   */
  public AttributesType getAttributes() {
    return attributes;
  }

  /**
   * set attributes.
   * @param attributesTypeParam attributes type to set
   */
  public void setAttributes(final AttributesType attributesTypeParam) {
    attributes = attributesTypeParam;
  }

  /**
   * process this elements styleId. this is used for controls and
   * changes the given styleId and the elements style id to a new
   * combined one.
   * @param element element
   * @param styleHandler style handler
   * @param controlDefinitionAttributes controlDefinitionAttributes
   * @param controlAttributes controlAttributes
   * @param nifty nifty
   * @param registeredEffects effects
   * @param time time
   */
  public static void applyControlAttributes(
      final Element element,
      final StyleHandler styleHandler,
      final Attributes controlDefinitionAttributes,
      final Attributes controlAttributes,
      final Nifty nifty,
      final Map < String, RegisterEffectType > registeredEffects,
      final TimeProvider time) {
    controlProcessStyleAttribute(
        element, styleHandler, controlDefinitionAttributes, controlAttributes, nifty, registeredEffects, time);
    controlProcessParameters(
        element, controlAttributes, nifty.getRenderDevice());
    for (Element child : element.getElements()) {
      applyControlAttributes(
          child, styleHandler, controlDefinitionAttributes, controlAttributes, nifty, registeredEffects, time);
    }
  }

  /**
   * process style attribute.
   * @param element element
   * @param styleHandler style handler
   * @param controlDefinitionAttributes control definition attributes
   * @param controlAttributes control attributes
   * @param nifty nifty
   * @param registeredEffects effects
   * @param time time
   */
  private static void controlProcessStyleAttribute(
      final Element element,
      final StyleHandler styleHandler,
      final Attributes controlDefinitionAttributes,
      final Attributes controlAttributes,
      final Nifty nifty,
      final Map < String, RegisterEffectType > registeredEffects,
      final TimeProvider time) {
    String myStyleId = element.getElementType().getAttributes().getStyle();
    if (myStyleId != null) {
      // this element has a style id set. is a special substyle?
      int indexOfSep = myStyleId.indexOf("#");
      if (indexOfSep != -1) {
        StyleType style = resolveStyle(styleHandler, myStyleId, controlAttributes.get("style"));
        if (style == null) {
          style = resolveStyle(styleHandler, myStyleId, controlDefinitionAttributes.get("style"));
        }
        if (style != null) {
          style.applyStyle(element, nifty, registeredEffects, time);
        }
      }
    }
  }

  /**
   * try to resolve style.
   * @param styleHandler style handler
   * @param myStyleId my style
   * @param newStyle new style
   * @return StyleType when resolved or null on error.
   */
  private static StyleType resolveStyle(
      final StyleHandler styleHandler,
      final String myStyleId,
      final String newStyle) {
    if (myStyleId.startsWith("#")) {
      String resolvedStyle = newStyle + myStyleId;

      // check if newStyleId exists.
      return styleHandler.getStyle(resolvedStyle);
    } else {
      return null;
    }
  }

  /**
   * process parameters.
   * @param element element
   * @param controlAttributes control attributes
   * @param niftyRenderEngine render engine
   */
  private static void controlProcessParameters(
      final Element element,
      final Attributes controlAttributes,
      final NiftyRenderEngine niftyRenderEngine) {
    for (Entry < String, String > entry
        : element.getElementType().getAttributes().findParameterAttributes().entrySet()) {
      String value = controlAttributes.get(entry.getValue());
      if (value == null) {
        value = "'" + entry.getValue() + "' missing o_O";
      }
      log.info("[" + element.getId() + "] setting [" + entry.getKey() + "] to [" + value + "]");
      Attributes attributes = new Attributes();
      attributes.overwriteAttribute(entry.getKey(), value);
      ElementType.applyAttributes(new AttributesType(attributes), element, niftyRenderEngine);
    }
  }
}

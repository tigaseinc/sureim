/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.other;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

/**
 *
 * @author andrzej
 */
public class TigaseMessengerPromoPanel extends Composite {
        
        public TigaseMessengerPromoPanel() {
        
                AbsolutePanel layout = new AbsolutePanel();
                
                Style style = layout.getElement().getStyle();
                //style.setColor("#333");
                style.setFloat(Style.Float.RIGHT);
                style.setProperty("clear", "right");
                style.setWidth(28, Style.Unit.PCT);
                style.setHeight(300, Style.Unit.PX);
                style.setMargin(2, Style.Unit.PCT);
                
                AbsolutePanel textPanel = new AbsolutePanel();
                textPanel.getElement().getStyle().setColor("#333");
                
                layout.add(textPanel);
                
                HeadingElement head = Document.get().createHElement(2);
                head.getStyle().setColor("#357AE8");
                head.getStyle().setProperty("padding", "0% 10%");
                
                head.setInnerText("Tigase Messenger on Android");
                
                textPanel.getElement().setInnerText("Some description needed here."
                        + "Some description needed here.Some description needed here.Some description needed here."
                        + "Some description needed here.Some description needed here.Some description needed here."
                        + "Some description needed here.Some description needed here.Some description needed here."
                        + "Some description needed here.Some description needed here.Some description needed here."
                        + "Some description needed here.");                
                textPanel.getElement().insertFirst(head);
                
                final Dictionary links = Dictionary.getDictionary("root");
                links.get("version-link-ads-free");
                
                Grid grid = new Grid(2,3);
                Label free = new Label("Free");
                style = free.getElement().getStyle();                
                style.setProperty("white-space", "nowrap");
                free.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                Window.open(links.get("version-link-free"), null, null);
                        }
                        
                });
                free.setStyleName("");
                grid.setWidget(0, 0, free);
                Label adsFree = new Label("Ads-Free");
                style = adsFree.getElement().getStyle();                
                style.setProperty("white-space", "nowrap");
                adsFree.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                Window.open(links.get("version-link-ads-free"), null, null);
                        }
                        
                });
                adsFree.setStyleName("");
                grid.setWidget(0, 2, adsFree);
                
                Image promo = new Image("mobile.png");
                promo.setHeight("100px");
                grid.setWidget(0, 1, promo);
                grid.getCellFormatter().getElement(0,1).setAttribute("rowspan", "2");
                grid.getCellFormatter().getElement(1,1).setAttribute("style", "display: none;");
                                
                Image image = new Image("android.jpg");
                image.setHeight("80px");
                image.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                Window.open(links.get("version-link-free"), null, null);
                        }
                        
                });
                grid.setWidget(1, 0, image);
                
                Image image1 = new Image("android.jpg");
                image1.setHeight("80px");
                image1.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                Window.open(links.get("version-link-ads-free"), null, null);
                        }
                        
                });
                grid.setWidget(1, 2, image1);

                grid.getElement().getStyle().setProperty("padding", "0% 14%");
                grid.getElement().getStyle().setColor("#357AE8");
                grid.getElement().getStyle().setProperty("text-align", "center");
                grid.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
                
                layout.add(grid);
                
                initWidget(layout);
        }
                
}

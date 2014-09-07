/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.settings;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

import tigase.sure.web.base.client.AppView;
import tigase.sure.web.base.client.widgets.VerticalTabLayoutPanel;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.events.ServerFeaturesChangedEvent;
import tigase.sure.web.site.client.events.ServerFeaturesChangedHandler;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule;
import tigase.jaxmpp.core.client.xmpp.modules.registration.InBandRegistrationModule;

/**
 *
 * @author andrzej
 */
public class SettingsViewImpl extends ResizeComposite implements SettingsView {
        
        private final ClientFactory factory;

        private VerticalTabLayoutPanel layout;
        private Collection<String> features;
 
        private GeneralSettingsView generalSettings;
        
//        private final ToogleButton messageArchivingButton;
        
        private final ServerFeaturesChangedHandler serverFeaturesChangedHandler = new ServerFeaturesChangedHandler() {

                public void serverFeaturesChanged(Collection<DiscoveryModule.Identity> identities, Collection<String> features_) {
                        features = features_;
                }
                
        };
        private final PersonalInformationView personalInformation;
        
        public SettingsViewImpl(ClientFactory factory_) {
                factory = factory_;
                
                AppView appView = new AppView(factory);
                appView.setActionBar(factory.actionBarFactory().createActionBar(this));

                layout = new VerticalTabLayoutPanel(factory, Style.Float.LEFT, 20, Unit.EM);

                generalSettings = new GeneralSettingsView(factory);
                
                layout.add(generalSettings, factory.i18n().generalSettings());

                personalInformation = new PersonalInformationView(factory);
                layout.add(personalInformation, factory.i18n().personalInformation());
                
                layout.itemSelected(0);
                
//                messageArchivingButton = new ToogleButton("Enabled", "Disabled");
//                layout.add(messageArchivingButton);
                
                appView.setCenter(layout);
                
                factory.eventBus().addHandler(ServerFeaturesChangedEvent.TYPE, serverFeaturesChangedHandler);
                
                initWidget(appView);                
        }

        public void refreshItems() {
                
//                messageArchivingButton.setDisabled(features.contains("urn:xmpp:archive:auto"));
                
        }
               
}

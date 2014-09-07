/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.Image;
import tigase.sure.web.base.client.AbstractAvatarFactory;
import tigase.sure.web.base.client.AvatarChangedEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule;
import tigase.jaxmpp.core.client.xmpp.modules.vcard.VCard;
import tigase.jaxmpp.core.client.xmpp.modules.vcard.VCardModule;
import tigase.jaxmpp.core.client.xmpp.modules.vcard.VCardModule.VCardAsyncCallback;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.jaxmpp.core.client.xmpp.stanzas.Presence;

/**
 *
 * @author andrzej
 */
public class AvatarFactory extends AbstractAvatarFactory {

        private static final Logger log = Logger.getLogger("AvatarFactory");
        private final ClientFactory factory;
//        private final MainCss css;
        private final Storage storage;

        public AvatarFactory(ClientFactory factory) {
                super(factory);
                this.factory = factory;
//                this.css = factory.getMainCss();
                this.storage = Storage.getLocalStorageIfSupported();
        }

        @Override
        public Image getAvatarForJid(final BareJID jid) {
                Image img = null;
                try {
                        Presence p = PresenceModule.getPresenceStore(factory.sessionObject()).getBestPresence(jid);
                        if (p != null) {
                                Element x = p.getChildrenNS("x", "vcard-temp:x:update");
                                if (x != null) {
                                        List<Element> photos = x.getChildren("photo");
                                        if (photos != null) {
                                                final String hash = photos.get(0).getValue();
                                                img = getAvatarForHash(hash);
                                                if (img == null) {
                                                        factory.jaxmpp().getModulesManager().getModule(VCardModule.class).retrieveVCard(JID.jidInstance(jid), new VCardAsyncCallback() {

                                                                @Override
                                                                protected void onVCardReceived(VCard vcard) throws XMLException {
                                                                        if (vcard.getPhotoVal() != null) {
                                                                                String avatar = "data:" + vcard.getPhotoType() + ";base64," + vcard.getPhotoVal();
                                                                                setAvatarForHash(hash, avatar);
                                                                        }
                                                                        factory.eventBus().fireEvent(new AvatarChangedEvent(JID.jidInstance(jid)));
                                                                }

                                                                @Override
                                                                public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
                                                                }

                                                                @Override
                                                                public void onTimeout() throws JaxmppException {
                                                                }
                                                        });
                                                }
                                        }
                                }
                        }

                        if (img == null) {
                                img = getImageFromStore(jid);
                        }
                }
                catch (Exception ex) {
                        log.log(Level.WARNING, "exception processing presence for avatar hash", ex);
                }

                if (img == null) {
                        img = new Image(factory.theme().socialPerson());
                }

                //img.setStyleName(css.avatarImageClass());

                return img;
        }

        @Override
        public Image getAvatarForHash(String hash) {
                String imgData = storage.getItem("avatar:hash:" + hash);
                if (imgData == null) {
                        return null;
                }

                return new Image(imgData);
        }

        @Override
        public void setAvatarForHash(String hash, String data) {
                storage.setItem("avatar:hash:" + hash, data);
        }

        private Image getImageFromStore(BareJID jid) {
                String imgData = storage.getItem("avatar:jid:" + jid.toString());
                if (imgData == null) {
                        return null;
                }

                return new Image(imgData);
        }

        @Override
        public void setAvatarForJid(BareJID jid, String data) {
                storage.setItem("avatar:jid:" + jid.toString(), data);
        }

        public void requestAvatar(final JID jid) {
                try {
                        factory.jaxmpp().getModulesManager().getModule(VCardModule.class).retrieveVCard(jid, new VCardAsyncCallback() {

                                @Override
                                protected void onVCardReceived(VCard vcard) throws XMLException {
                                        if (vcard.getPhotoVal() != null) {
                                                String avatar = "data:" + vcard.getPhotoType() + ";base64," + vcard.getPhotoVal();
                                                setAvatarForJid(jid.getBareJid(), avatar);
                                        }
                                        factory.eventBus().fireEvent(new AvatarChangedEvent(jid));
                                }

                                @Override
                                public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
                                        throw new UnsupportedOperationException("Not supported yet.");
                                }

                                @Override
                                public void onTimeout() throws JaxmppException {
                                        throw new UnsupportedOperationException("Not supported yet.");
                                }
                                
                        });
                }
                catch (Exception ex) {
                        Logger.getLogger("AvatarFactory").log(Level.WARNING, "exception requesting vCard avatar", ex);
                }
        }
}

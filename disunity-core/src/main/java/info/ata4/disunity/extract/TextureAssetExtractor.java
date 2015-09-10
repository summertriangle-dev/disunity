/*
 ** 2015 September 08
 **
 ** (I will go along with the original copyright notice...)
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.disunity.extract;

import info.ata4.io.buffer.ByteBufferUtils;
import info.ata4.log.LogUtils;
import info.ata4.unity.rtti.ObjectData;
import info.ata4.unity.util.UnityClass;
import info.ata4.unity.rtti.FieldNode;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.nio.channels.FileChannel;
import java.io.FileOutputStream;
import java.nio.file.Path;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class TextureAssetExtractor extends AbstractAssetExtractor {
    
    private static final Logger L = LogUtils.getLogger();
    
    @Override
    public boolean isEligible(ObjectData objectData) {
        boolean yes = super.isEligible(objectData);
        L.log(Level.WARNING, "class {0}!", objectData.info().unityClass());
        return yes;
    }
    
    @Override
    public UnityClass getUnityClass() {
        return new UnityClass("Texture2D");
    }

    @Override
    public void extract(ObjectData objectData) throws IOException {
        L.log(Level.INFO, "Working on ''{0}''", objectData.instance().getString("m_Name"));
        L.log(Level.INFO, "{0}x{1}", new Object[] {objectData.instance().getUInt32("m_Width"), objectData.instance().getUInt32("m_Height")});
        for (FieldNode node : objectData.instance()) {
            L.log(Level.DEBUG, "{0}", node.getType());
        }
        L.log(Level.INFO, "{0}", objectData.instance().getUInt32("m_CompleteImageSize"));
        
        Path outFile = getOutputDirectory().resolve(objectData.instance().getString("m_Name") + ".ahff");
        File file = outFile.toFile();
        FileChannel channel = new FileOutputStream(file, false).getChannel();
        
        ByteBuffer hdr = ByteBuffer.allocate(16);
        hdr.order(ByteOrder.LITTLE_ENDIAN);
        hdr.putInt(0, objectData.instance().getSInt32("m_Width"));
        hdr.putInt(4, objectData.instance().getSInt32("m_Height"));
        hdr.putInt(8, objectData.instance().getSInt32("m_CompleteImageSize"));
        hdr.putInt(12, objectData.instance().getSInt32("m_TextureFormat"));
        channel.write(hdr, 0);
        channel.write(objectData.instance().getChild("image data").getChildValue("data", ByteBuffer.class), 16);
        channel.close();
    }
    
}

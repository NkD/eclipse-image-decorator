package cz.nkd.eclipse.image.decorator;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Eclipse image decorator
 * 
 * @author Michal NkD Nikodim [michal.nikodim@gmail.com]
 */
public class EclipseImageDecorator implements ILabelDecorator {

    private static final String[] IMAGE_EXTENSIONS = { ".png", ".jpg", ".bmp", ".ico", ".gif" };

    @Override
    public void addListener(ILabelProviderListener listener) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
    }

    @Override
    public Image decorateImage(Image image, Object element) {
        if (element instanceof IResource) {
            IResource resource = (IResource) element;
            if (isImageFile(resource)) {
                Image resourceImage = getImage(resource);
                if (resourceImage == null) {
                    return null;
                }
                if (resourceImage.getBounds().width > 16 || resourceImage.getBounds().height > 16) {
                    ImageData orig = resourceImage.getImageData();
                    ImageData scaled = null;
                    int originalWidth = orig.width;
                    int originalHeight = orig.height;
                    if (originalWidth >= originalHeight) {
                        scaled = orig.scaledTo(16, (originalHeight * 16) / originalWidth);
                    } else {
                        scaled = orig.scaledTo((originalWidth * 16) / originalHeight, 16);
                    }
                    return ImageDescriptor.createFromImageData(scaled).createImage();
                } else {
                    return resourceImage;
                }
            }
        }
        return null;
    }

    @Override
    public String decorateText(String text, Object element) {
        return null;
    }

    private static boolean isImageFile(IResource resource) {
        String fileName = resource.getName().toLowerCase();
        for (String extension : IMAGE_EXTENSIONS) {
            if (fileName.endsWith(extension)) return true;
        }
        return false;
    }

    public static Image getImage(IResource resource) {
        FileInputStream fis = null;
        try {
            String absolutePath = resource.getLocation().toOSString();
            if (absolutePath == null) {
                return null;
            }
            fis = new FileInputStream(absolutePath);
            Image image = new Image(Display.getDefault(), fis);
            return image;
        } catch (Exception e) {
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }

}

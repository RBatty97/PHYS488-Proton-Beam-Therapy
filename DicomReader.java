//Importing the needed libraries to handle the DICOM files.
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.TagFromName;
import com.idrsolutions.image.dicom.DicomDecoder;
import java.io.File;
import java.awt.image.BufferedImage;

public class DicomReader {

  private static AttributeList list;
  private static DicomDecoder decoder = new DicomDecoder();
  private BufferedImage decodedImage;

  public DicomReader(File dicomFile) {
      this.list = new AttributeList();
      //this.decoder = new DicomDecoder();
      try {
        //reading the DICOM file so that imformation can be extracted.
        this.list.setDecompressPixelData(false);
        this.list.read(dicomFile);
        this.decodedImage = decoder.read(dicomFile);
      }
      //eror handling in the case of a bad read.
      catch(Exception e) {
        System.out.println("Error");
        this.decodedImage = null;
        e.printStackTrace();
      }
  }

  public double getSliceLocation() {
    return Double.valueOf(this.getTagInformation(TagFromName.SliceLocation));
  }

  //returns the image height of the image in pixels.
  public int getHeight() {return this.decodedImage.getHeight();}

  //returns the width of the image in pixels.
  public int getWidth() {return this.decodedImage.getWidth();}

  //returns the rgb values of the specific pixel (r,g and b all the same for a greyscale image)
  public int[] getRGB(int x, int y) {
    //forumulas to convert the data from a single int to rgb values.
    int data = this.decodedImage.getRGB(x, y);
    int r = (int) ((Math.pow(256, 3) + data) / 65536);
    int g = (int) (((Math.pow(256, 3) + data) / 256) % 256);
    int b = (int) ((Math.pow(256, 3) + data) % 256);
    int[] rgb = {r, g, b};
    return rgb;
  }

  //returns pixel height in mm. (z)
  public double getPixelHeight() {
    String data = getTagInformation(TagFromName.PixelSpacing);
    String[] heightWidth = data.split("\\\\");
    return Double.valueOf(heightWidth[0]);
  }

  //returns pixel width in mm. (x)
  public double getPixelWidth() {
    String data = getTagInformation(TagFromName.PixelSpacing);
    String[] heightWidth = data.split("\\\\");
    return Double.valueOf(heightWidth[1]);
  }

  //returns slice thickness in mm. (y)
  public double getSliceThickness() {
    return Double.valueOf(getTagInformation(TagFromName.SliceThickness));
  }

  //method for reading certain tag information from the DICOM file.
  private String getTagInformation(AttributeTag attrTag) {
    return Attribute.getDelimitedStringValuesOrEmptyString(this.list, attrTag);
  }
}

//Stamp of Approval for FinalModel

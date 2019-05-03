import java.io.File;
import java.util.Random;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class Scan {

  private Random randGen = new Random();

  private int[][][] pixelData; //pixel rgb data [y][x][z]
  private double[][][] densityData; //voxel density data [y][x][z]
  private double[][][] eLossMag; //count of energy lost in each voxel [y][x][z]
  private int[][][] cancerData; //work in progress, do not use
  private double[] xyz0 = new double[3]; //botton front left cornet of whole scan.
  private double[] xyz1 = new double[3]; //top right back corner of whole scan.
  private double height; //height of scan in m.
  private double width; //width of scan in m.
  private double depth; //depth of scan in m.
  private double pixelHeight; //height in m of each individual pixel.
  private double pixelWidth; //width in m of each individual pixel.
  private double sliceThickness; //thickness of each slice in m.
  private int counter; //do not use, work in progress.
  private String[][] sliceLocations;

  public Scan(double[] position, String directory) {
    this.xyz0 = position;
    this.generate(directory);
    this.xyz1[0] = this.xyz0[0] + this.width;
    this.xyz1[1] = this.xyz0[1] + this.height;
    this.xyz1[2] = this.xyz0[2]+ this.depth;
    //System.out.println(this.xyz1[2]);
    this.counter = 0;
  }

  //getters
  public int[][][] getPixelData() {return this.pixelData;}
  public double[][][] getDensityData() {return this.densityData;}
  public double[][][] getELossData() {return this.eLossMag;}
  public int[][][] getCancerData() {return this.cancerData;}
  public double[] getXYZ0() {return this.xyz0;}
  public double[] getXYZ1() {return this.xyz1;}
  public double getHeight() {return this.height;}
  public double getWidth() {return this.width;}
  public double getDepth() {return this.depth;}
  public double getPixelHeight() {return this.pixelHeight;}
  public double getPixelWidth() {return this.pixelWidth;}
  public double getSliceThickness() {return this.sliceThickness;}

  //method to generate the voxels based on a directory of DICOM files.
  public void generate(String directory) {
    File folder = new File(directory);
    File[] files = folder.listFiles(); //list of all files in directory
    //give the data arrays some size. Y height = number of files (each file is one slice)
    this.pixelData = new int[files.length][][];
    this.densityData = new double[files.length][][];
    this.eLossMag = new double[files.length][][];
    this.cancerData = new int[files.length][][];

    sliceLocations = new String[files.length][2];
    for (int i = 0; i < files.length; i++) {
      double location = new DicomReader(files[i]).getSliceLocation();
      sliceLocations[i][0] = files[i].getName();
      sliceLocations[i][1] = String.valueOf(location);
    }
    sort(sliceLocations);

    //loop over every file
    System.out.println("Generating Voxels");
    for (int y = 0; y < files.length; y++) {
      if (y % (files.length / 10) == 0 && (y != 0)) {
        //System.out.println(((y / files.length) * 100) % 10);
        System.out.print("#");
      }
      //System.out.println(sliceLocations[y][1] + " : " + sliceLocations[y][0]);
      File file = new File(directory + "\\" + sliceLocations[y][0]);
      DicomReader reader = new DicomReader(file);
      //System.out.println(y);

      //assigning / calculating scan properties
      this.pixelHeight = reader.getPixelHeight() * 0.001;
      this.pixelWidth = reader.getPixelWidth() * 0.001;
      this.sliceThickness = reader.getSliceThickness() * 0.001;
      this.height = files.length * this.sliceThickness;
      this.width = this.pixelWidth * reader.getWidth();
      this.depth = this.pixelHeight * reader.getHeight();
      //System.out.println(this.sliceThickness);
      //System.out.println(this.height + " : " + this.width + " : " + this.depth);
      //preparing each slice for each data array.
      int[][] pixelSlice = new int[reader.getWidth()][reader.getHeight()];
      double[][] slice = new double[reader.getWidth()][reader.getHeight()];
      double[][] eLossSlice = new double[reader.getWidth()][reader.getHeight()];
      int[][] cancerSlice = new int[reader.getWidth()][reader.getHeight()];

      //loop over each slice (xz plane).
      for (int x = 0; x < reader.getWidth(); x++) {
        for (int z = 0; z < reader.getHeight(); z++) {
          //fill all of the slices.
          pixelSlice[(reader.getWidth() - x) - 1][z] = reader.getRGB(x, z)[0];
          slice[(reader.getWidth() - x) - 1][z] = getDensityFromRGB(reader.getRGB(x, z)[0]);
          //slice[x][(reader.getHeight() - z) - 1] = 1;
          eLossSlice[x][z] = 0;
          cancerSlice[x][z] = 0;
        }
      }
      //place each slice into the 3D arrays.
      this.pixelData[(files.length - y) - 1] = pixelSlice;
      this.densityData[(files.length - y) - 1] = slice;
      this.eLossMag[y] = eLossSlice;
      this.cancerData[y] = cancerSlice;
    }
    System.out.println();
    System.out.println("Voxels generated");
  }

  public void sort(String[][] array) {
    int swaps = 0;
    for (int i = 0; i < array.length - 1; i++) {
      double location1 = Double.valueOf(array[i][1]);
      double location2 = Double.valueOf(array[i+1][1]);
      if ((location1 - location2) < 0) {
        swaps++;
        String[] temp = array[i];
        array[i] = array[i+1];
        array[i+1] = temp;
      }
    }
    if (swaps > 0) {
      sort(array);
    }
  }

  //work out if a proton is in the scan as a whole.
  public boolean isInScan(Proton p) {
    double[] position = p.getLastPosition();
    if (position[1] >= this.xyz0[0] && position[2] >= this.xyz0[1] && position[3] >= this.xyz0[2]) {
      if (position[1] <= this.xyz1[0] && position[2] <= this.xyz1[1] && position[3] <= this.xyz1[2]) {
        return true;
      }
    }
    return false;
  }

  //retuns the index of the voxel that the proton is inside.
  public int[] getVolume(Proton p) {
    double[] position = p.getLastPosition();
    int x = (int) Math.floor((position[1] - this.xyz0[0]) / this.pixelWidth);
    int y = (int) Math.floor((position[2] - this.xyz0[1]) / this.sliceThickness);
    int z = (int) Math.floor((position[3] - this.xyz0[2]) / this.pixelHeight);
    int[] index = {x, y, z};
    //System.out.println(x + " : " + y + " : " + z);
    return index;
  }

  //returns the density value of the voxel which the proton is inside.
  public double getDensity(Proton p) {
    int[] index = this.getVolume(p);
    //System.out.println(densityData[index[1]][index[0]][index[2]]);
    return densityData[index[1]][index[0]][index[2]];
  }

  //calculates density value based on RGB value of voxel (min and max are based on fat tissue adnd bone respectively);
  public double getDensityFromRGB(int colour) {
    if (colour == 0) {
      //System.out.println("here");
      return 0.001225;
    }
    //System.out.println(colour);
    double min = 0.9094;
    double max = 1.75;
    //System.out.println(colour);
    return min + (((double) (colour - 75) / 256) * (max - min));
  }

  //calculates energy loss depending on which voxel the proton is in.
  public void doELoss(Proton p) {
    EnergyLoss ELoss = new EnergyLoss(new Material(getDensity(p), 6, 12));
    double energyLoss = ELoss.getEnergyLoss(p) * p.getLastDistance();
    int[] index = this.getVolume(p);
    this.eLossMag[index[1]][index[0]][index[2]] += energyLoss;
    p.reduceEnergy(energyLoss);
  }

  //calculates MCS dpeneding on which voxel the proton is in.
  public void doMCS(Proton p) {
    MCS multScatter = new MCS(new Material(getDensity(p), 6, 12));
    double theta0 = multScatter.getTheta0(p);
    p.applySmallRotation((theta0 * randGen.nextGaussian()), (theta0 * randGen.nextGaussian()));
  }

  //Work in progress, do not use.
  public void findTumour() {
    for (int y = 0; y < pixelData.length; y++) {
      int[][] slice = pixelData[y];
      double average = 0;
      int count = 0;
      for (int x = 0; x < slice.length; x++) {
        for (int z = 0; z < slice.length; z++) {
          if (pixelData[y][x][z] != 0) {
            average += pixelData[y][x][z];
            count++;
          }
        }
      }
      average /= count;
      this.counter = 0;
      //System.out.println("Checking: " + y);
      here: for (int i = 0; i < slice.length; i++) {
        for (int j = 0; j < slice.length; j++) {
          int[] index = {i, y, j};
          if (check(index, average) == true) {
            break here;
          }
        }
      }
    }
  }

  //work in progress, do not use.
  public boolean check(int[] index, double average) {
    int shade = this.pixelData[index[1]][index[0]][index[2]];
    if (shade - average >= 30) {
      //System.out.println("found one");
      this.cancerData[index[1]][index[0]][index[2]]++;
      this.counter++;
      for(int x = index[0] - 1; x <= index[0] +1; x++) {
        for (int z = index[2] - 1; z <= index[2] + 1; z++) {
          if (x < 0 || x >= this.cancerData[0].length) {
            continue;
          } else if (z < 0 || z >= this.cancerData[0].length) {
            continue;
          } else {
            if (this.cancerData[index[1]][x][z] != 0) {
              this.cancerData[index[1]][index[0]][index[2]]++;
            } else {
              int[] nextIndex = {index[1], x, z};
              this.check(nextIndex, average);
              if (this.cancerData[index[1]][x][z] != 0) {
                this.cancerData[index[1]][index[0]][index[2]]++;
              }
            }
          }
        }
      }
    }
    //System.out.println(this.counter);
    if (this.counter >= 1000) {
      System.out.println("Success!");
      return true;
    } else {
      return false;
    }
  }

  public void generateCSV(String data, String filename, int slice) throws IOException {
    FileWriter file = new FileWriter(filename);
    PrintWriter outputFile = new PrintWriter(file);

    if (data == "pixelData") {
      int[][] pixelSlice = this.pixelData[slice];

      for (int o = pixelSlice.length - 1; o >= 0; o--) {
        for (int p = 0; p < pixelSlice[0].length; p++) {
          outputFile.print(pixelSlice[p][o] + ",");
        }
        outputFile.println();
      }
    } else if (data == "densityData") {
      double[][] densitySlice = this.densityData[slice];
      System.out.println(sliceLocations[slice][1]);
      for (int k = densitySlice.length - 1; k >= 0; k--) {
        for (int l = 0; l < densitySlice[0].length; l++) {
          outputFile.print(densitySlice[l][k] + ",");
        }
        outputFile.println();
      }
    } else if (data == "eLossData") {
      double[][] eLossSlice = this.eLossMag[slice];

      for (int m = eLossSlice.length - 1; m >= 0; m--) {
        for (int n = 0; n < eLossSlice[0].length; n++) {
          outputFile.print(eLossSlice[n][m] + ",");
        }
        outputFile.println();
      }
    }

    outputFile.flush();
    outputFile.close();
    file.close();
  }
}

//Stamp of Approval for FinalModel

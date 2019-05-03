import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class Geometry {
  private Cuboid[] cuboids;
  private int volumes;
  private Scan scan;
  private double[] outputData;

  //Creates a Geometry object which represents all of the geometries involved in the experiment.
  public Geometry(int maxVolumes) {
    this.cuboids = new Cuboid[maxVolumes];
    this.volumes = 0;
  }

  public Scan getScan() {return this.scan;}

  //add a cuboid with origin point at xyz0 and furthest point (diagonal) at xyz1.
  public void addCuboid(double[] xyz0, double[] xyz1, Material material) {
    Cuboid cuboid = new Cuboid(xyz0, xyz1, material);
    cuboids[volumes] = cuboid;
    volumes++;
  }

    public double getPixelHeight() {
      return this.scan.getPixelHeight();
    }

  public double[] getOutputData() {
    double[][][] data = this.scan.getELossData();
    double[][] eLossData = new double[data[0].length][data[0].length];
    for (int n = 0; n < data[0].length; n++) {
      for (int m = 0; m < data[0][0].length; m++) {
        eLossData[n][m] = 0;
      }
    }
    for (int i = 0; i < data.length; i++) {
      double[][] slice = data[i];
      for (int j = data[0][0].length - 1; j >= 0; j--) {
        for (int k = 0; k < data[0].length; k++) {
          eLossData[k][j] += slice[k][j];
        }
      }
    }
    outputData = new double[data[0][0].length];
    for (int z = data[0][0].length - 1; z >= 0; z--) {
      double sum = 0;
      for (int x = 0; x < data[0].length; x++) {
        sum += eLossData[x][z];
      }
      outputData[z] = sum;
    }
    return outputData;
  }

  //adds a scan (voxel network based on MRI data), should only ever have one.
  public void addScan(double[] xyz0, String directory) {
    this.scan = new Scan(xyz0, directory);
  }

  //calculates energy loss of proton based on the protons position, calculates which volume the proton is in.
  public boolean doELoss(Proton p) {
    if (this.scan.isInScan(p)) {
      this.scan.doELoss(p);
      return true;
    } else {
      for (int i = volumes - 1; i >= 0; i--) {
        if (this.cuboids[i].isInVolume(p)) {
          this.cuboids[i].doELoss(p);
          return true;
        }
      }
      return false;
    }
  }

  //same as ELoss but for MCS
  public void doMCS(Proton p) {
    if (this.scan.isInScan(p)) {
      this.scan.doMCS(p);
      return;
    } else {
      for (int i = volumes - 1; i >= 0; i--) {
        if (this.cuboids[i].isInVolume(p)) {
          this.cuboids[i].doMCS(p);
          return;
        }
      }
    }
  }

  public void writeToDiskOld(String filename) throws IOException {
    FileWriter file = new FileWriter(filename);     // this creates the file with the given name
    PrintWriter outputFile = new PrintWriter(file); // this sends the output to file1
    double[][][] data = this.scan.getELossData();
    double[][] eLossData = new double[data[0].length][data[0].length];
    for (int n = 0; n < data[0].length; n++) {
      for (int m = 0; m < data[0][0].length; m++) {
        eLossData[n][m] = 0;
      }
    }
    for (int i = 0; i < data.length; i++) {
      double[][] slice = data[i];
      for (int j = data[0][0].length - 1; j >= 0; j--) {
        for (int k = 0; k < data[0].length; k++) {
          eLossData[k][j] += slice[k][j];
        }
      }
    }
    for (int z = data[0][0].length - 1; z >= 0; z--) {
      for (int x = 0; x < data[0].length; x++) {
        outputFile.print(eLossData[x][z] + ",");
      }
      outputFile.println();
    }
    outputFile.close(); // close the output file
  }

  public void writeToDiskNew(String simValue) throws IOException {
    double[][][] data = this.scan.getELossData();
    for (int y=0; y < data.length; y++) {
      String filename = simValue + "DataDump" + Integer.toString(y) + ".csv";
      FileWriter file = new FileWriter(filename);     // this creates the file with the given name
      PrintWriter outputFile = new PrintWriter(file); // this sends the output to file1
      double[][] slice = data[y];
      for (int z = data[0].length - 1; z >= 0; z--) {
        for (int x = 0; x < data[0].length; x++) {
          outputFile.print(x + "," + y + "," + z + "," + slice[x][z]);
          outputFile.println();
        }
      }
      outputFile.close(); // close the output file
    }
  }

  public void generateCSV(String data, String filename, int slice) throws IOException {
    this.scan.generateCSV(data, filename, slice);
  }
}

//Stamp of Approval for FinalModel

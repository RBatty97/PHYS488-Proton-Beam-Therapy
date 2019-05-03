import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

class RunSimulation
{
  private static Geometry experiment;
  private static int noPeaks = 1;
  private static Random randGen = new Random();
  private static double[][] data = new double[noPeaks][];

  static Scanner keyboard = new Scanner(System.in);

  public static void main (String [] args) throws IOException {

    //Quality of Life Additions
    System.out.println("Initial Kinetic Energy?");
    double initialKE = keyboard.nextDouble();
    int protonCount = 10000;
    int nev = 10000;

    //Momentum Calculator
    int naturalC = 1; //Speed of light in natural units
    double restMass = 938.3; //Proton rest mass in MeV
    double AshLorentz = (initialKE/(restMass*naturalC*naturalC))+1; //Calculation of Lorentz factor
    //System.out.println("The lorentz of this beam is " +AshLorentz);
    double calc = (naturalC*naturalC)*(1-(1/(AshLorentz*AshLorentz)));
    double velocity = Math.sqrt(calc);
    double initialMomentum = AshLorentz*restMass*velocity;
    //System.out.println("The momentum of this beam is " +initialMomentum+ " MeV");
    String DataDumpYN = keyboard.nextLine();
    for (int i = 0; i < noPeaks; i++) {
      BeamGeometry beam = setupBeam(noPeaks, i);
      experiment = setupExperiment();

      System.out.println("Running Simulation " + nev + " times");
      for (int n = 0; n < nev; n++){
        if (n % (nev / 10) == 0) {
          System.out.print("#");
        }
        double[] randXY = getXY(0.005);
        double[] position0 = {0., randXY[0], randXY[1], 0.};
        double[] momentum0 = {0., 0., initialMomentum};
        Proton proton = new Proton(position0, momentum0, 10000, 1E-8);
        double[] state = proton.propogate(beam);

        if (state != null) {
          double[] position = {0., state[1], state[2], 0.};
          double[] momentum = {state[4], state[5], state[6]};
          proton = new Proton(position, momentum, 10000, 1E-8);
          proton.propogate(experiment);
        }
      }
      data[i] = experiment.getOutputData();
      experiment.writeToDiskOld("test" + Integer.toString(i) + ".csv");
      if (DataDumpYN.equals("Y")) {
        experiment.writeToDiskNew(Integer.toString(i)); }
        System.out.println();
        System.out.println("Simulation " + (i + 1) + " completed");
      }
      generateBraggPeak("Bragg.csv", experiment);
      System.out.println("done");
    }

  public static Geometry setupExperiment() {
    Geometry experiment = new Geometry(1);
    Material air = new Material(0.001225, 7 , 14);

    double[] position1 = {-1., -1, 0.};
    double[] position2 = {1., 1, 1};
    double[] position3 = {-0.2, -0.15, 0.00}; //Will require trial and error.

    experiment.addCuboid(position1, position2, air);
    experiment.addScan(position3, "C:\\Development\\Python\\Programs\\brain_test"); //Change file name
    return experiment;
  }

/*
  public static NewBeamGeometry setupBeam(int noPeaks, int i){
    NewBeamGeometry newBeamExperiment = new NewBeamGeometry(4);
    Material lead = new Material(11.34, 82, 207); //remember Material is density g/cm^3,
    Material tungsten = new Material(19.3, 74, 184);
    Material air = new Material(0.001225, 7, 14);
    Material perspex = new Material(1.18, 54, 100);
    Material aluminium = new Material(2.70, 13, 26.981);
*/
    // BASIC BEAM SINGLE SCATTER SET UP

/*
        double[] position1 = { 0., 0., 0}; // (x, y, z) beam enclosure origin position
        double[] position2 = { 0., 0., 0.03}; //origin position of the lead scattering plate
        double[] position3 = { 0., 0., 0.45}; //origin position of the tungsten collimator
        double[] position4 = {0, 0, 0}; //origin position of the range modulator
        double[] position5 = {0, 0 , 0.75};
*/
    //DOUBLE SCATTER WITH DUEL RING

/*
        double[] position4 = {0, 0, 0}; //origin position of the range modulator
        double[] position5 = {0., 0., 0};
        double[] position6 = {0., 0., 0.002};
        double[] position7 = {0., 0., 0.30};
        double[] position8 = {0., 0., 0.30};
        double[] position9 = {0., 0., 0.75};
*/


/*
        double[] modulatorThickness = new double[noPeaks];
        //sets minimum range modulator thickness - value can be tweaked.
        double minThickness = 0.002;
        //sets the incrimental increase in thickness for the range modulator ring - value can be tweaked
        double increment = 0.0015;
*/

/*
          for (int j = 0; j < noPeaks; j++) {
          modulatorThickness[j] = minThickness + (j * increment); //fills the values for the thickness of the range modulator
        }
*/

/*
        for (int j = 0; j < noPeaks; j++) {

          modulatorThickness[j] = (j + 1) * increment;
        } //fills the values for the thickness of the range modulator
*/


    // BASIC BEAM SINGLE SCATTER SET UP
    // Remember it's inner Radius, outer Radius, Thickness, position

/*
        newBeamExperiment.addCylinder(0, 2, 1, air, position1); //Beam enclosure
        newBeamExperiment.addCylinder(0, 1, 0.005, lead, position2); //scattering plate
        newBeamExperiment.addCylinder(0.03, 1, 0.05, tungsten, position3); //collimator (first value can be edited, it is the collimator radius)
        newBeamExperiment.addCylinder(0, 1, modulatorThickness[i], perspex, position4); //range modulator
        newBeamExperiment.addContour(perspex, 2, 0.5, position5);
*/
    //DOUBLE SCATTER WITH DUEL RING

/*
        beamExperiment.addCylinder(0, 1, modulatorThickness[i], perspex, position4); //range mod
        beamExperiment.addCylinder(0., 2, 3 , air, position5); // air cylinder
        beamExperiment.addCylinder(0., 1, 0.02, lead, position6); // first scatterer
        beamExperiment.addCylinder(0, 0.10, 0.0082, aluminium, position7); //inner ring second scatterer
        beamExperiment.addCylinder(0.10, 0.30, 0.005, lead, position8); //outer ring second scatterer
        beamExperiment.addCylinder(0.15, 1.0, 0.5, lead, position9); // collimator
  */
        //radIn , radOut, height, material, origin


    //DOUBLE SCATTER WITH DUEL RING 2


      //  return newBeamExperiment; }



  public static BeamGeometry setupBeam(int noPeaks, int i){
    BeamGeometry beamExperiment = new BeamGeometry(4); //REMEMBER TO CHANGE TO AVOID BOUNDS ERRORS
    Material lead = new Material(11.34, 82, 207); //remember Material is density g/cm^3,
    Material tungsten = new Material(19.3, 74, 184);
    Material air = new Material(0.001225, 7, 14);
    Material perspex = new Material(1.18, 54, 100);
    Material aluminium = new Material(2.70, 13, 26.981);

// BASIC BEAM SINGLE SCATTER SET UP


    double[] position1 = { 0., 0., 0}; // (x, y, z) beam enclosure origin position
    double[] position2 = { 0., 0., 0.003}; //origin position of the lead scattering plate
    double[] position3 = { 0., 0., 0.45}; //origin position of the tungsten collimator
    double[] position4 = {0, 0, 0}; //origin position of the range modulator

//DOUBLE SCATTER WITH DUEL RING
/*
    double[] position4 = {0, 0, 0}; //origin position of the range modulator
    double[] position5 = {0., 0., 0};
    double[] position6 = {0., 0., 0.002};
    double[] position7 = {0., 0., 0.30};
    double[] position8 = {0., 0., 0.30};
    double[] position9 = {0., 0., 0.75};
*/


//MODULATOR THICKNESS

    double[] modulatorThickness = new double[noPeaks];
    //sets minimum range modulator thickness - value can be tweaked.

    /*
    double minThickness = 0.1;
    //sets the incremental increase in thickness for the range modulator ring - value can be tweaked
    double increment = 0.0025;

      for (int j = 0; j < noPeaks; j++) {
      modulatorThickness[j] = minThickness + (j * increment); //fills the values for the thickness of the range modulator

      */
    double RangeShifter = 0.1; //Sets the maximum distance (the point where most energy loss occurs)
    double increment = 0.0015; //Sets the incremental distance for the range modulator ring
      for (int j = 0; j < noPeaks; j++) {
      modulatorThickness[j] = RangeShifter - (j * increment);
    }

/*
     for (int j = 0; j < noPeaks; j++) {

      modulatorThickness[j] = (j + 1) * increment;
    } //fills the values for the thickness of the range modulator
*/


// BASIC BEAM SINGLE SCATTER SET UP
// Remember it's inner Radius, outer Radius, Thickness, position


    beamExperiment.addCylinder(0, 1, 0.5, air, position1); //Beam enclosure
    beamExperiment.addCylinder(0, 1, 0.005, lead, position2); //scattering plate
    beamExperiment.addCylinder(0.03, 1, 0.5, tungsten, position3); //collimator (first value can be edited, it is the collimator radius)
    beamExperiment.addCylinder(0, 1, modulatorThickness[i], perspex, position4); //range modulator


//DOUBLE SCATTER WITH DUEL RING

/*
    beamExperiment.addCylinder(0, 1, modulatorThickness[i], perspex, position4); //range mod
    beamExperiment.addCylinder(0., 2, 3 , air, position5); // air cylinder
    beamExperiment.addCylinder(0., 1, 0.02, lead, position6); // first scatterer
    beamExperiment.addCylinder(0, 0.10, 0.0082, aluminium, position7); //inner ring second scatterer
    beamExperiment.addCylinder(0.10, 0.30, 0.005, lead, position8); //outer ring second scatterer
    beamExperiment.addCylinder(0.15, 1.0, 0.5, lead, position9); // collimator
*/
    //radIn , radOut, height, material, origin





    return beamExperiment;
  }


  //generates a single csv which contains the summed data for each z value for all the values of the range modulator thickness (make a different line of the graph with each collumn)
  public static void generateBraggPeak(String filename, Geometry experiment) throws IOException {
    FileWriter file = new FileWriter(filename);
    PrintWriter outputFile = new PrintWriter(file);

    for (int i = 0; i < data[0].length; i++) {
      outputFile.print((i+1)*experiment.getPixelHeight()+",");
      for (int j = 0; j < data.length; j++) {
        outputFile.print(data[j][i] + ",");
      }
      outputFile.println();
    }
    outputFile.flush();
    outputFile.close();
    file.close();
  }

  //functions for calculating randomized proton starting position
  public static double getRand(double max) {
    double x = -(max / 2) + (randGen.nextDouble() * max);
    return x;
  }

  public static double[] getXY(double max) {
    double x = getRand(max);
    double y = getRand(max);
    double r = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    double[] xy = {x, y};
    if (r <= max) {
      return xy;
    } else {
      max = r;
      x = getRand(max);
      y = getRand(max);
      double[] newXY = {x, y};
      return newXY;
    }
  }
}

//Stamp of Approval for FinalModel

import java.util.Random;

public class Cuboid {

    Random randGen = new Random();

    private double[] xyz0;
    private double[] xyz1;
    private Material material;

    public Cuboid(double[] xyz0, double[] xyz1, Material material) {
        this.xyz0 = xyz0; //bottom front left corner (origin)
        this.xyz1 = xyz1; //top right back corner.
        this.material = material;
    }

    //method which evaluates whether a proton is inside the cuboid.
    public boolean isInVolume(Proton p) {
        double[] position = p.getLastPosition();
        if (position[1] >= this.xyz0[0] && position[2] >= this.xyz0[1] && position[3] >= this.xyz0[2]) {
            if (position[1] <= this.xyz1[0] && position[2] <= this.xyz1[1] && position[3] <= this.xyz1[2]) {
                return true;
            }
        }
        return false;
    }

    //caluclates energy loss of a proton in the cuboid.
    public void doELoss(Proton p) {
        EnergyLoss ELoss = new EnergyLoss(this.material);
        p.reduceEnergy(ELoss.getEnergyLoss(p) * p.getLastDistance());
    }

    //calculates MCS of a proton in the cuboid.
    public void doMCS(Proton p) {
        MCS multScatter = new MCS(this.material);
        double theta0 = multScatter.getTheta0(p);
        p.applySmallRotation((theta0 * randGen.nextGaussian()), (theta0 * randGen.nextGaussian()));
    }
}

//Stamp of Approval for FinalModel

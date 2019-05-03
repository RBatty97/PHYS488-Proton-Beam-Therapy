import java.util.Random;

public class EnergyLoss
{
  Random randGen = new Random();
  final double K = 0.307075; //MeVcm^2
  final double me = 0.511; //MeV
  private double I; //MeVZ
  private double rho;
  private double Z;
  private double A;
  private double smear = 0.1;

  public EnergyLoss(Material material) {
    this.rho = material.getRho();
    this.Z = material.getZ();
    this.A = material.getA();
    I = 0.0000135 * Z;
  }

  public double getEnergyLoss(Proton p) {
    if (this.A == 0) {
      return 0;
    }
    double z = p.getCharge();
    double M = p.getMass();
    double beta = p.getBeta();
    double gamma = p.getGamma();
    double term1 = 2 * me * Math.pow(beta, 2) * Math.pow(gamma, 2);
    double Wmax = term1 / (1 + ((2 * gamma * me) / (M + Math.pow((me / M), 2))));
    double term2 = K * Math.pow(z, 2) * this.rho * (this.Z / this.A) * (1 / Math.pow(beta, 2));
    double term3 = (term1 * Wmax) / Math.pow(I, 2);
    double E_avg = (term2 * (0.5 * Math.log(term3) - Math.pow(beta, 2))) * 100;
    return (E_avg + (smear * randGen.nextGaussian()));
  }
}

//Stamp of Approval for FinalModel

public class MCS
{
  private double thickness;
  private double rho;
  private double Z;
  private double A;

  public MCS(Material material) {
    this.rho = material.getRho();
    this.Z = material.getZ();
    this.A = material.getA();
    }

  public double getTheta0(Proton p) {
    if (this.A == 0) {
      return 0;
    }
    double term1 = (13.6 / (p.getBeta() * p.getLastMomentumMag()));
    double term2 = p.getCharge() * Math.sqrt((p.getLastDistance()) / this.getX0()) * (1 + (0.038 * Math.log((p.getLastDistance()) / this.getX0())));
    return term1 * term2;
  }

  public double getX0() {
    if (this.A == 0) {
      return 0;
    }
    return ((716.4 * this.A) / (this.rho * this.Z * (this.Z + 1) * Math.log(287 / Math.sqrt(this.Z)))) / 100;
  }
}

//Stamp of Approval for FinalModel

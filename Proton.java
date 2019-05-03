class Proton
{
  // a simple class to store properties of a particle as well as its history

  //Particle specific constants
  private double m0;    // rest mass (MeV)
  private int charge;   // particle charge (in units of elementary charge)
  private double c = 3E8;
  // Fourposition and fourmomenta: from start to end
  private double[][] position;
  private double[][] momentum;
  private int laststep; // how many steps the particle has made
  private int maxSteps;
  private double dt; //time increment
  // constructor to initialise the particle
  // and make space to store the trajectory
  public Proton(double [] position0, double [] momentum0, int maxSteps, double time)
  {
    this.maxSteps = maxSteps;
    dt = time/maxSteps;
    position = new double[maxSteps+1][4];
    momentum = new double[maxSteps+1][3];
    laststep = 0;
    position[0] = position0;
    momentum[0] = momentum0;
    m0 = 938.27231;
    charge = 1;
  }


  public void propogate(Geometry experiment) {
    double mydt = dt;
    for (int i = 0; i < this.maxSteps; i++) {
      double[] currentPosition = getLastPosition();
      double[] currentMomentum = getLastMomentum();
      double E = getLastE();
      double[] Yf = new double[7];
      double[] v1 = {c * currentMomentum[0] / E, c * currentMomentum[1] / E, c * currentMomentum[2] / E};

      Yf[0] = currentPosition[0] + mydt;
      for(int j = 1; j < 4; j++){
        Yf[j] = currentPosition[j] + mydt * v1[j-1];
        Yf[j+3] = currentMomentum[j-1];
      }
      this.updateParticle(Yf);
      if (experiment.doELoss(this)) {
        if (getLastMomentumMag() == 0) {
          //System.out.println("Proton stopped");
          break;
        } else {
          experiment.doMCS(this);
        }
      } else {
        //System.out.println("Proton left");
        break;
      }
    }
  }

  public double[] propogate(BeamGeometry beam) {
    double mydt = dt;
    for (int i = 0; i < this.maxSteps; i++) {
      double[] currentPosition = getLastPosition();
      double[] currentMomentum = getLastMomentum();
      double E = getLastE();
      double[] Yf = new double[7];
      double[] v1 = {c * currentMomentum[0] / E, c * currentMomentum[1] / E, c * currentMomentum[2] / E};

      Yf[0] = currentPosition[0] + mydt;
      for(int j = 1; j < 4; j++){
        Yf[j] = currentPosition[j] + mydt * v1[j-1];
        Yf[j+3] = currentMomentum[j-1];
      }
      this.updateParticle(Yf);

      if (beam.doELoss(this)) {
        if (getLastMomentumMag() == 0) {
          //System.out.println("Proton stopped");
          return null;
        } else {
          beam.doMCS(this);
        }
      } else {
        if (0.5 - this.getLastZ() <= 0.05) {
          //System.out.println("Proton left");
          return this.getLastState();
        } else {
          return null;
        }
      }
    }
    //System.out.println("Didn't make it");
    return null;
  }

  // return properties of particle
  public int getCharge() {return charge;}
  public double getMass() {return m0;}
  public int getSteps() {return laststep;}

  // Lorentz calculations for beta and gamma
  public double getBeta() { return getLastMomentumMag()/getLastE(); }
  public double getGamma() { return getLastE()/getMass(); }

  // calculate distance in space of last step
  public double getLastDistance()
  {
    double [] last = getLastPosition();
    double [] previous = getPosition(laststep-1);
    double dist = 0.;
    for (int i = 1; i < 4; i++) {
      double delta = last[i]-previous[i];
      dist += delta*delta;
    }
    return Math.sqrt(dist);
  }

  // set the next position+momentum
  public void updateParticle(double[] state)
  {
    laststep = laststep+1;
    for(int i=0; i<4; i++){
      position[laststep][i] = state[i];
    }
    for(int i=0; i<3; i++){
      momentum[laststep][i] = state[i+4];
    }
  }

  public void undoLastStep()
  {
    laststep = laststep-1;
  }

  // return the last position and momentum stored
  public double[] getPosition(int step) {return position[step];}
  public double[] getLastPosition() {return position[laststep];}

  public double[] getMomentum(int step) {return momentum[step];}
  public double[] getLastMomentum() {return momentum[laststep];}

  // return components of last position and momentum stored
  public double getLastT() {return position[laststep][0];}
  public double getLastX() {return position[laststep][1];}
  public double getLastY() {return position[laststep][2];}
  public double getLastZ() {return position[laststep][3];}
  public double getLastPx() {return momentum[laststep][0];}
  public double getLastPy() {return momentum[laststep][1];}
  public double getLastPz() {return momentum[laststep][2];}

  // return position and momentum of step i
  public double[] getState(int i)
  {
    double[] txyz_pxpypz = new double[7];
    for(int j=0; j<4; j++){
      txyz_pxpypz[j] = position[i][j];
    }
    for(int j=0; j<3; j++){
      txyz_pxpypz[j+4] = momentum[i][j];
    }
    return txyz_pxpypz;
  }
  public double[] getLastState()
  {
    return getState(laststep);
  }

  public double getMomentumMag(int step)
  {
    double[] txyz_pxpypz = getState(step);
    double p = 0.;
    for (int j = 0; j < 3; j++) {
      p += txyz_pxpypz[j+4]*txyz_pxpypz[j+4];
    }
    p = Math.sqrt(p);
    return p;
  }
  public double getLastMomentumMag()
  {
    return getMomentumMag(laststep);
  }

  public double getE(int step)
  {
    double p = getMomentumMag(step);
    if (p == 0) {
      return getMass();
    } else {
      return Math.sqrt(m0*m0+p*p);
    }
  }

  public double getLastE()
  {
    return getE(laststep);
  }

  // the following methods return one big array
  // with all positions/momenta
  public double[] getAllPosMom(int coo) {
    double[] all = new double[laststep];
    for (int istep = 0; istep < laststep; istep++) {
      if (coo < 4)
      all[istep] = position[istep][coo];
      else if (coo < 7)
      all[istep] = momentum[istep][coo-4];
      else if (coo == 7)
      all[istep] = getMomentumMag(istep);
      else if (coo == 8)
      all[istep] = getE(istep);
    }
    return all;
  }
  public double[] getAllT() {return getAllPosMom(0);}
  public double[] getAllX() {return getAllPosMom(1);}
  public double[] getAllY() {return getAllPosMom(2);}
  public double[] getAllZ() {return getAllPosMom(3);}
  public double[] getAllPx() {return getAllPosMom(4);}
  public double[] getAllPy() {return getAllPosMom(5);}
  public double[] getAllPz() {return getAllPosMom(6);}
  public double[] getAllP() {return getAllPosMom(7);}
  public double[] getAllE() {return getAllPosMom(8);}


  public void applySmallRotation(double dtheta_xz, double dtheta_yz)
  {
    // calculate new momentum direction
    // approximates this into two sequential x-z and y-z changes
    double p, theta;

    double[] mom = getLastMomentum();

    p = Math.sqrt(mom[0]*mom[0] + mom[2]*mom[2]);
    theta = Math.atan2(mom[0], mom[2]) + dtheta_xz;
    momentum[laststep][0] = p*Math.sin(theta);
    momentum[laststep][2] = p*Math.cos(theta);

    p = Math.sqrt(mom[1]*mom[1] + mom[2]*mom[2]);
    theta = Math.atan2(mom[1], mom[2]) + dtheta_yz;
    momentum[laststep][1] = p*Math.sin(theta);
    momentum[laststep][2] = p*Math.cos(theta);
  }

  public void reduceEnergy(double Eloss)
  {
    // reduce energy of particle while keeping direction the same
    double E = getLastE();
    if (Eloss > E) {
      momentum[laststep][0] = 0.;
      momentum[laststep][1] = 0.;
      momentum[laststep][2] = 0.;
    } else if (E-Eloss <= getMass()) {
      //System.out.println("Energy lower than mass");
      momentum[laststep][0] = 0.;
      momentum[laststep][1] = 0.;
      momentum[laststep][2] = 0.;
    } else {
      double pnew = Math.sqrt(Math.pow(E-Eloss, 2) - Math.pow(getMass(),2));
      double factor = pnew/getLastMomentumMag();
      //System.out.println(pnew);
      momentum[laststep][0] *= factor;
      momentum[laststep][1] *= factor;
      momentum[laststep][2] *= factor;
    }
  }
}
//Stamp of Approval for FinalModel

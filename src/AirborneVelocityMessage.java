



public class AirborneVelocityMessage extends AdsMessage{
	//int messageSubTypeD;
										
	//Constructor for Subtype 1/2:
	public AirborneVelocityMessage(String binarySentence, int messageTypeD,int originatorD,long time) 
	{
		super(binarySentence,messageTypeD, originatorD, time);
		//this.messageSubTypeD = Integer.parseInt(binarySentence.substring(37,40),2);

	}

	
	
	//GENERAL getTER METHODS / DECODING
	public int getVerticalRateSource()
	{
		return Integer.parseInt(binarySentence.substring(35,36),2);
		//0..geometry source, 1.. barometric source
	}
	public int getVerticalRateSign()
	{
		return Integer.parseInt(binarySentence.substring(36,37),2);
		//0..going up, 1.. going down
	}
	public int getVerticalRate()//nochmal (factor-1)*64 überprüfen
	{
		int factor = Integer.parseInt(binarySentence.substring(37,46),2);
		if(factor == 0)
			return -1;
		else
			return (factor-1)*64;
	}
	public boolean getGeoGreaterBaroAttitude()
	{
		switch(Integer.parseInt(binarySentence.substring(48,49),2))
		{
		case 0: return true;
		case 1: return false;
		default: System.out.println("Error at GeoGreaterBaro");	return false;
		}
	}
	//Returns the Difference (in ft) between BarometricAltitude and BarometricAltitude
	public int getDeltaBaroAltoAttitude()
	{
		int delta = Integer.parseInt(binarySentence.substring(49,56),2);
		if(delta == 0)
		{
			System.out.println("Error at getDeltaBaroAltoAttitude");
			return -1;
		}
		else
		{
			if(this.getGeoGreaterBaroAttitude())
				return (delta-1)*25;
			else
				return -(delta-1)*25;
		}
	}
	//END GENERAL getTER MEHTODS / DECODING
	
	
	//SUBTYPE 1/2 getter Methods / Decoding
	public double getNavigationAccuracy()
	{
		// Parsing Navigation Accuracy Bits 10-12
		int navAccFactor = Integer.parseInt(binarySentence.substring(10,13),2);
		switch(navAccFactor)
		{
		case 0: return 100;
		case 1: return 10;
		case 2: return 3;
		case 3: return 1;
		case 4: return 0.3;
		default:return -1;
		}
	}
	public boolean getMovingEast()
	{
		switch(getMessageSubType())
		{
		case 1:
		case 2: return !(Boolean.parseBoolean(binarySentence.substring(13,14)));
		default: System.out.println("Error in isMovingEast");return false;
		}
	}
	public boolean getMovingWest()
	{
		switch(getMessageSubType())
		{
		case 1:
		case 2: return BooleanHelper.string2boolean(binarySentence,13);
		default: System.out.println("Error in getMovingWest");return false;
		}
		
	}
	public int getEastWestVelocity()
	{
		int knots = Integer.parseInt(binarySentence.substring(14,24),2);
		
		if(knots > 0)
			return knots-1;
		else
			{
				System.out.println("Error at getEastWestVelocity");
				return -1;
			}
	}
	public boolean getMovingSouth()
	{
		switch(getMessageSubType())
		{
		case 1: case 2:return BooleanHelper.string2boolean(binarySentence,24);
		default: System.out.println("Error in getMovingSouth"); return false;
		}
		
	}
	public int getNorthSouthVelocity()
	{
		int knots = Integer.parseInt(binarySentence.substring(25,35),2);
		
		if(knots > 0)
			return knots-1;
		else
			{
				System.out.println("Error at getEastWestVelocity");
				return  -1;
			}
	}
	//END Subtype 1/2 getter Methods / Decoding
	
	//SUBTYPE 3/$ getter Methods / Decoding
	//SUBTYPE 3/4 Methods

	public boolean getHeadingStatusAvailable()
	{
		return Boolean.parseBoolean(binarySentence.substring(13,14));
	}
	public double getHeadingDegrees()
	{
		double angle=0;
		switch(getMessageSubType())
		{
		case 1: case 2:
			angle = Math.atan((double)getNorthSouthVelocity()/getEastWestVelocity())*180/Math.PI;	
			if(getMovingSouth())
			{
				if(getMovingWest()) //Moving South-West
					angle = 270-angle;
				else					//Moving South-East
					angle += 90;	
			}
			else
			{
				if(getMovingWest()) //Moving North-West
					angle += 270;
				else					//Moving North-East
					angle = 90-angle;
			}
			break;
		case 3: case 4:
			int factor = Integer.parseInt(binarySentence.substring(14,24),2);
			angle = factor*360.0/1024.0;
			break;
		}
		return angle;
	}
	public boolean getTrueAirspeedType()
	{
		return Boolean.parseBoolean(binarySentence.substring(24,25));
	}
	public double getAirspeed()
	{
		double airspeed=0.0;
		switch(getMessageSubType())
		{
		case 1: case 2:
			airspeed = Math.sqrt(Math.pow((double)getEastWestVelocity(), 2)+Math.pow((double)getNorthSouthVelocity(),2));
			break;
		case 3: case 4:
			int knots = Integer.parseInt(binarySentence.substring(25,35),2);
			if(knots > 0)
				airspeed = knots-1;
			else
			{
				System.out.println("Error at getAirspeed()");
				airspeed = -1;
			}
			break;
		}
		return airspeed;
	}
	public int getMessageSubType()
	{
		return Integer.parseInt(binarySentence.substring(5,8),2);
	}
	//END SUBTYPE 3/4 getter Methods / Decoding
	public void print()
	{
		/*System.out.println("-------------------------------------------");
		DateFormat simpleDate = new SimpleDateFormat("EEEE', 'dd.MM.YYYY', 'H:m:ss.S");
		System.out.println("Message(Sub)Type: \t\t("+messageSubTypeD+")"+messageTypeD);
		System.out.println("Originator: \t\t\t"+originatorD);
		System.out.println("Zeit: \t\t\t\t"+simpleDate.format(tStamp));*/
		super.print();
		System.out.println("MessageSubType: \t\t\t"+getMessageSubType());   
		System.out.println("navigationAccuracy: \t\t"+ getNavigationAccuracy());
		//System.out.println("heading Degrees: "+ headingDegrees);
		System.out.println("vertical Rate Source: \t\t"+ this.getVerticalRateSource());
		System.out.println("vertical Rate Sign: \t\t" + this.getVerticalRateSign());
		System.out.println("Difference Baro-Alto: \t\t"+this.getDeltaBaroAltoAttitude());
		switch(getMessageSubType())
		{
		case 1: case 2:		System.out.println("movingWest: \t\t\t"+ this.getMovingWest());
							System.out.println("movingSouth: \t\t\t"+this.getMovingSouth());
							System.out.println("EastWestVelocity: \t\t"+ this.getEastWestVelocity());
							System.out.println("NorthSouthVelocity: \t\t"+ this.getNorthSouthVelocity());
							break;
		case 3:	case 4:		System.out.println("Airspeed: \t\t"+ this.getAirspeed());
							System.out.println("Heading Status: \t\t"+ this.getHeadingStatusAvailable());
							System.out.println("Heading Degrees: \t\t"+this.getHeadingDegrees());
							System.out.println("isTrueAirspeed: \t\t"+this.getTrueAirspeedType());
							break;
		default:			System.out.println("----UNKNOWN SUBTYPE-----");break;
		}
		
	}
	public String toString()
	{
		String res = super.toString();
		res += ", Airsp.: "+getAirspeed()+ ", H-Degr: "+getHeadingDegrees();
		return res;
	}
	public String toJedisString()
	{
		String res = super.toJedisString();
		//res += getAirspeed()+";"+getHeadingDegrees()+":";
		res += ";"+getAirspeed()+";"+getHeadingDegrees();
		return res;
	}


	public String toString()
	{
		return super.toString()+
		", VerticalRateSource(): " +getVerticalRateSource()+
		", VerticalRateSign(): " +getVerticalRateSign()+
		", VerticalRate(): " +getVerticalRate()+
		", GeoGreaterBaroAttitude(): " +getGeoGreaterBaroAttitude()+
		", DeltaBaroAltoAttitude(): " +getDeltaBaroAltoAttitude()+
		", NavigationAccuracy(): " +getNavigationAccuracy()+
		", MovingEast(): " +getMovingEast()+
		", MovingWest(): " +getMovingWest()+
		", EastWestVelocity(): " +getEastWestVelocity()+
		", MovingSouth(): " +getMovingSouth()+
		", NorthSouthVelocity(): " +getNorthSouthVelocity()+
		", HeadingStatusAvailable(): " +getHeadingStatusAvailable()+
		", HeadingDegrees(): " +getHeadingDegrees()+
		", TrueAirspeedType(): " +getTrueAirspeedType()+
		", Airspeed(): " +getAirspeed()+
		", MessageSubType(): " +getMessageSubType()+
	}/* toString() hinzugefuegt - glkeit00 */
}

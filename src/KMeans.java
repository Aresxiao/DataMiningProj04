import java.util.ArrayList;
import java.util.HashMap;

import org.omg.CORBA.PUBLIC_MEMBER;


public class KMeans {
	
	int k;
	int totalWords;
	double[][] seeds;
	
	public KMeans(int k,int totalWords){
		this.k = k;
		this.totalWords = totalWords;
		seeds = new double[k][totalWords];
	}
	
	public double euclideanDistance(double[] v1,double[] v2){
		double sum=0;
		for(int i=0;i<v1.length;i++){
			sum+=(v1[i]-v2[i])*(v1[i]-v2[i]);
		}
		//sum=sum/v1.length;
		return Math.sqrt(sum);
	}
	
	public void generateSeeds(double[][] sample){
		int len = sample.length;
		int firstSeedRow=(int) (Math.random()*len);
		//int seedRowCount=0;
		int seedRowFlag=0;
		for(int j = 0;j<totalWords;j++){
			seeds[seedRowFlag][j] = sample[firstSeedRow][j];
		}
		seedRowFlag++;
		while(seedRowFlag<k){
			double[] dis=new double[len];
			for(int i=0;i<len;i++){
				dis[i]=0;
				double min=euclideanDistance(seeds[0], sample[i]);
				for(int count = 0;count<seedRowFlag;count++){
					double d = euclideanDistance(seeds[count], sample[i]);
					if(min>d){
						min=d;
					}
				}
				dis[i]=min;
			}
			double sum=0;
			for(int i=0;i<len;i++){
				sum+=dis[i];
			}
			
			double ran = Math.random()*sum;
			int row = 0;
			for(int i=0;i<len;i++){
				ran=ran-dis[i];
				if(ran<=0){
					row=i;
					break;
				}
			}
			for(int j = 0;j<totalWords;j++){
				seeds[seedRowFlag][j] = sample[row][j];
			}
			seedRowFlag++;
		}
		
	}
	
	public void kMeansClustering(double[][] trainPost){
		HashMap<Integer, Integer> postToClusterMap = new HashMap<Integer,Integer>();
		//for(int i=0;i<)
		ArrayList<Integer>[] clusterLists = new ArrayList[k];		//这样写可能有一点问题。
		int cycleCount=0;
		while(cycleCount<500){
			for(int i=0;i<k;i++){
				clusterLists[i].clear();
			}
			for(int i=0;i<trainPost.length;i++){
				double min = euclideanDistance(trainPost[i], seeds[0]);
				int row=0;
				for(int s=0;s<k;s++){
					double d = euclideanDistance(trainPost[i], seeds[i]);
					if(min>d){
						min=d;
						row=s;
					}
				}
				postToClusterMap.put(i, row);
				clusterLists[row].add(i);
			}
			
			for(int i=0;i<k;i++){
				for(int j=0;j<totalWords;j++){
					double sum = 0;
					for(int s=0;s<clusterLists[i].size();s++){
						int row = clusterLists[i].get(s);
						sum+=trainPost[row][j];
					}
					seeds[i][j] = sum/clusterLists[i].size();
				}
			}
			cycleCount++;
		}
	}
	
	
}

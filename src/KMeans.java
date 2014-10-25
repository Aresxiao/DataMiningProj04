import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.omg.CORBA.PUBLIC_MEMBER;


public class KMeans {
	
	int k;
	int totalWords;
	double[][] seeds;
	
	double[] postVectorLength;
	
	public KMeans(int k,int totalWords){
		this.k = k;
		this.totalWords = totalWords;
		seeds = new double[k][totalWords];
		System.out.println("k="+k+"totalWord="+totalWords);
	}
	
	public double consineDistance(double[] v1,double[] v2){
		double sum=0;
		sum = innerProduct(v1, v2)/(vectorLength(v1)*vectorLength(v2));
		return sum;
	}
	
	public double innerProduct(double[] v1,double[] v2){	//内积
		double product=0;
		for(int i=0;i<v1.length;i++){
			product += v1[i]*v2[i];
		}
		return product;
	}
	
	public double vectorLength(double[] v1){		//向量模长
		double d = 0;
		for(int i = 0;i<v1.length;i++)
			d+=v1[i]*v1[i];
		return Math.sqrt(d);
	}
	
	
	public void generateSeeds(double[][] sample){
		postVectorLength = new double[sample.length];
		for(int i = 0;i < sample.length; i++){
			postVectorLength[i] = vectorLength(sample[i]);
		}
		
		int len = sample.length;
		System.out.println("长度为:"+len);
		HashSet<Integer> set = new HashSet<Integer>();
		for(int i=0;i<k;i++){
			
			int row = (int) (Math.random()*len);
			while(set.contains(row)){
				row = (int) (Math.random()*len);
			}
			set.add(row);
			System.out.println("generateSeeds : "+row);
			for(int j=0;j<totalWords;j++){
				seeds[i][j] = sample[row][j];
			}
		}
		
		
		/*
		int len = sample.length;
		HashSet<Integer> set = new HashSet<Integer>();
		int count=0;
		int fr = (int) (Math.random() * len);
		for(int j = 0;j < totalWords;j++){
			seeds[count][j] = sample[fr][j];
		}
		set.add(fr);
		count++;
		while(count<k){
			double[] dis = new double[len];
			for(int i = 0;i < len;i++){
				double max = 0;
				
				for(int s = 0;s<count;s++){
					double d = innerProduct(sample[i], seeds[s])/(postVectorLength[i]*vectorLength(seeds[s]));
					if(max<d){
						max = d;
					}
				}
				dis[i] = max;
			}
			int r = 0;
			for(int i = 0;i < len;i++){
				if(dis[r]>dis[i])
					r = i;
			}
			for(int j = 0;j < totalWords;j++){
				seeds[count][j] = sample[r][j];
			}
			count++;
		}
		*/
		
	}
	
	
	
	public ArrayList<HashSet<Integer>> kMeansClustering(double[][] trainPost){
		
		ArrayList<HashSet<Integer>> clusterSetList = new ArrayList<HashSet<Integer>>();
		for(int i=0;i<k;i++){
			HashSet<Integer> set = new HashSet<Integer>();
			clusterSetList.add(set);
		}
		int cycleCount=0;
		int flag = 0;
		while(cycleCount<6){
			for(int i=0;i<k;i++){
				clusterSetList.get(i).clear();
			}
			for(int i=0;i<trainPost.length;i++){
				int row=0;
				double min = consineDistance(trainPost[i], seeds[0]);
				//System.out.println("第"+i+"个数据最小值为"+min);
				for(int j = 1;j<k;j++){
					double d = innerProduct(trainPost[i], seeds[j]) / (postVectorLength[i]*vectorLength(seeds[j]));
					if(d > min){
						min = d;
						row = j;
					}
				}
				clusterSetList.get(row).add(i);
			}
			
			for(int i = 0;i < k;i++){
				for(int j = 0;j < totalWords;j++){
					double sum = 0;
					Iterator<Integer> iterator = clusterSetList.get(i).iterator();
					while(iterator.hasNext()){
						int r = iterator.next();
						sum += trainPost[r][j];
					}
					seeds[i][j] = sum / clusterSetList.get(i).size();
				}
			}
			
			System.out.println("----------------------------");
			for(int i=0;i<k;i++){
				System.out.println("第"+i+"个集合的大小为"+clusterSetList.get(i).size());
			}
			cycleCount++;
		}
		
		
		return clusterSetList;
		
	}
	
	
}

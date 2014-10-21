import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FastSearch {
	
	double dc;
	int totalPost;
	int totalTheme;
	int totalWords;
	
	double[] desity;
	double[] distance;
	double[][] center;
	ArrayList<ArrayList<Integer>> postPerThemeList = new ArrayList<ArrayList<Integer>>();
	Map<Integer, Integer> postToThemeMap = new HashMap<Integer,Integer>();
	
	
	public FastSearch(int totalPost,int totalTheme,int totalWords){
		dc=0.1;
		this.totalPost = totalPost;
		this.totalTheme = totalTheme;
		this.totalWords = totalWords;
		
		desity = new double[totalPost];
		distance = new double[totalPost];
		center = new double[totalTheme][totalWords];
		for(int i=0;i<totalTheme;i++){
			ArrayList<Integer> list = new ArrayList<Integer>();
			postPerThemeList.add(list);
		}
	}
	
	public void localDensity(double[][] trainPost){		//计算密度。
		
		for(int i=0;i<trainPost.length;i++){
			for(int x=0;x<trainPost.length;x++){
				if(x!=i){
					double dis = euclideanDistance(trainPost[i], trainPost[x]);
					if(dis<dc){
						desity[i]+=1;
					}
				}
			}
		}
	}
	
	public double euclideanDistance(double[] v1,double[] v2){
		double sum=0;
		for(int i=0;i<v1.length;i++){
			sum+=(v1[i]-v2[i])*(v1[i]-v2[i]);
		}
		return Math.sqrt(sum);
	}
	
	public void distanceFromHigherDensity(double[][] trainPost){	//计算所有的点的距离
		
		for(int i=0;i<trainPost.length;i++){		//对于一般的点，距离的定义是取比该点局部密度大的所有点的最小距离
			
			for(int x=0;x<trainPost.length;x++){
				if(i!=x&&desity[x]>desity[i]){
					double dis = euclideanDistance(trainPost[i], trainPost[x]);
					if(distance[i]==0){
						distance[i]=dis;
					}
					else{
						if(distance[i]>dis){
							distance[i]=dis;
						}
					}
				}
			}
		}
		
		//对于密度最大的点，delta值是别的点到该点的最大的距离。
		int max=0;
		for(int i=0;i<trainPost.length;i++){
			if(desity[max]<desity[i]){
				max=i;
			}
		}
		for(int i=0;i<trainPost.length;i++){
			if(i!=max){
				double dis = euclideanDistance(trainPost[max], trainPost[i]);
				if(distance[max]<dis)
					distance[max]=dis;
			}
		}
	}
	
	public void selectCenter(double[][] trainPost){		//这里可能不对
		double[] product = new double[totalPost];
		//int[] record = new int[totalPost];
		Map<Double, Integer> valueRowMap = new HashMap<Double, Integer>();
		for(int i=0;i<totalPost;i++){
			product[i]=desity[i]*distance[i];
			valueRowMap.put(product[i], i);
		}
		for(int i=0;i<totalPost;i++){
			int max=i;
			for(int j=i;j<totalPost;j++){
				if(product[j]>product[max]){
					max=j;
				}
			}
			if(max!=i){
				double t = product[i];
				product[i] = product[max];
				product[max] = t;
				
			}
		}
		
		for(int i=0;i<totalTheme;i++){
			
			int row = valueRowMap.get(product[i]);
			for(int j=0;j<totalWords;j++){
				center[i][j] = trainPost[row][j];
				ArrayList<Integer> list = postPerThemeList.get(i);
				list.add(row);
				postToThemeMap.put(row, i);
			}
			
		}
		
	}
	
	public void calCenterPerPost(double[][] trainPost){
		
		for(int i=0;i<trainPost.length;i++){
			
		}
	}
	
}




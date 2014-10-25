import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FastSearch {
	
	double dc;
	int totalPost;
	int totalTheme;
	int totalWords;
	
	double[] desity;		//记录每个点的密度
	
	double[] distance;		//记录距离
	double[][] center;
	int[] recordDensity;
	double[] postLength;		//用来保存每条帖子的模长
	
	ArrayList<ArrayList<Integer>> postPerThemeList = new ArrayList<ArrayList<Integer>>();
	Map<Integer, Integer> postToThemeMap = new HashMap<Integer,Integer>();
	ArrayList<HashSet<Integer>> postToThemeSets = new ArrayList<HashSet<Integer>>();
	
	double[][] vectorProduct;
	
	
	public FastSearch(int totalPost,int totalTheme,int totalWords){
		dc=0.86;
		this.totalPost = totalPost;
		this.totalTheme = totalTheme;
		this.totalWords = totalWords;
		
		
		desity = new double[totalPost];
		distance = new double[totalPost];
		center = new double[totalTheme][totalWords];
		recordDensity = new int[totalPost];
		postLength = new double[totalPost];
		for(int i=0;i<totalTheme;i++){
			ArrayList<Integer> list = new ArrayList<Integer>();
			postPerThemeList.add(list);
			HashSet<Integer> set = new HashSet<Integer>();
			postToThemeSets.add(set);
		}
	}
	
	public void localDensity(double[][] trainPost){		//计算密度。
		for(int i=0;i<trainPost.length;i++){
			postLength[i] = vectorLength(trainPost[i]);
			distance[i] = 2.1;
		}
		
		vectorProduct = new double[trainPost.length][trainPost.length];
		
		for(int i = 0;i<trainPost.length;i++){
			for(int j = 0;j<trainPost.length;j++){
				vectorProduct[i][j] = innerProduct(trainPost[i], trainPost[j]);
				vectorProduct[j][i] = vectorProduct[i][j];
			}
		}
		for(int i=0;i<trainPost.length;i++){
			recordDensity[i]=i;
			for(int x=0;x<trainPost.length;x++){
				if(x!=i){
					double dis =1 - vectorProduct[i][x]/(postLength[i]*postLength[x]);
					if(dis<dc){
						desity[i]+=1;
					}
				}
			}
		}
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
	
	public void distanceFromHigherDensity(double[][] trainPost){	//计算所有的点的距离
		
		for(int i=0;i<trainPost.length;i++){		//对于一般的点，距离的定义是取比该点局部密度大的所有点的最小距离
			
			for(int x=0;x<trainPost.length;x++){
				if(i!=x&&(desity[x]>desity[i])){
					double dis =1 - vectorProduct[i][x]/(postLength[i]*postLength[x]);
					
					if(distance[i]>dis){
						distance[i]=dis;
					}
				}
			}
		}
		
		//对于密度最大的点，delta值是为3。
		int max=0;
		for(int i=0;i<trainPost.length;i++){
			if(desity[max]<desity[i]){
				max=i;
			}
		}
		distance[max]=3;
		
	}
	
	public void selectCenter(double[][] trainPost){		//选取中心点
		
		double[] tempDensity = desity.clone();
		int[] record = new int[totalPost];
		for(int i = 0;i<totalPost;i++)
			record[i] = i;
		for(int i = 0;i<totalPost;i++){
			int max = i;
			for(int j = i+1;j<totalPost;j++){
				if(tempDensity[max]<tempDensity[j])
					max = j;
			}
			if(max!=i){
				double t = tempDensity[max];
				tempDensity[max] = tempDensity[i];
				tempDensity[i] = t;
				
				int rec = record[max];
				record[max] = record[i];
				record[i] = rec;
			}
		}
		int count = 0;
		for(int i = 0;i<totalPost;i++){
			
			int line = record[i];
			if(distance[line]>0.79){
				for(int j = 0;j<totalWords;j++){
					center[count][j] = trainPost[line][j];
				}
				
				ArrayList<Integer> list = postPerThemeList.get(count);
				list.add(line);
				postToThemeMap.put(line, count);
				postToThemeSets.get(count).add(line);
				count++;
			}
			
			if(count>=10)
				break;
		}
		
	}
	
	public ArrayList<HashSet<Integer>> calCenterPerPost(double[][] trainPost){
		for(int i=0;i<totalPost;i++){
			int max=i;
			for(int j=i;j<totalPost;j++){
				if(desity[max]<desity[j])
					max=j;
			}
			if(max!=i){
				double temp = desity[i];
				desity[i] = desity[max];
				desity[max] = temp;
				
				int rec = recordDensity[i];
				recordDensity[i] = recordDensity[max];
				recordDensity[max] = rec;
			}
		}
		
		for(int i=0;i<trainPost.length;i++){
			int row = recordDensity[i];
			if(!postToThemeMap.containsKey(row)){
				int min=0;
				for(int j=0;j<i;j++){
					int rj = recordDensity[j];
					int rmin = recordDensity[min];
					double dmin = 1 - vectorProduct[rmin][row]/(postLength[rmin]*postLength[row]);
					double dj = 1 - vectorProduct[rj][row]/(postLength[rj]*postLength[row]);
					if(dmin>dj){
						min=j;
					}
				}
				min = recordDensity[min];
				int theme = postToThemeMap.get(min);
				postToThemeMap.put(row, theme);
				postPerThemeList.get(theme).add(row);
				postToThemeSets.get(theme).add(row);
			}
		}
		
		return postToThemeSets;
	}
	
}




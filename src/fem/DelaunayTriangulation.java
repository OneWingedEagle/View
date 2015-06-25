package fem;

/**
 * TODO Put here a description of what this class does.
 *
 * @author Hassan.
 *         Created Mar 11, 2013.
 */
import java.awt.Point;
import java.util.Iterator;
import java.util.TreeSet;

import main.Main;
import math.Vect;
import math.util;


public class DelaunayTriangulation
{
   int[][] adjMatrix;
   Vect[] P;

   DelaunayTriangulation(int size)
   {
     this.adjMatrix = new int[size][size];
   }
   
   DelaunayTriangulation(Vect[] P)
   {
	   this.P=P;
	   int size=P.length;
     this.adjMatrix = new int[size][size];
   }
   
	public static void main(String[] args){
		
		int N=4;
		Vect[] P=new Vect[N];
/*		for(int k=0;k<N;k++){
			P[k]=new Vect(100*Math.random(),100*Math.random(),0);
		}*/

		P[0]=new Vect(0,0);
		P[1]=new Vect(60,0);
		P[2]=new Vect(70,50);
		P[3]=new Vect(0,50);
		DelaunayTriangulation dt=new DelaunayTriangulation(P);
		
		TreeSet<GraphEdge> ts=dt.getEdges();

		Iterator<GraphEdge> it=ts.iterator();
		
		util.pr(ts.size());
		//util.show(dt.adjMatrix);
	/*	while(it.hasNext()){
			GraphEdge eg=(GraphEdge) it.next();
			util.pr(eg.n1+"  "+eg.n2);
		}*/
	}
   
   
   public int[][] getAdj() {
     return this.adjMatrix;
   }

   public TreeSet<GraphEdge> getEdges()
   {
     TreeSet<GraphEdge> result = new TreeSet<GraphEdge>();

     int n=this.P.length;
     if (n == 2)
     {
       this.adjMatrix[0][1] = 1;
       this.adjMatrix[1][0] = 1;
    
       result.add(new GraphEdge(0,1));

       return result;
     }

     for (int i = 0; i < n - 2; i++) {
       for (int j = i + 1; j < n; j++) {
    	   
         for (int k = i + 1; k < n; k++)
         {
           if (j == k) {
             continue;
           }

           Vect normal=P[j].sub(P[i]).cross(P[j].sub(P[k]));
           
           boolean flag=false;
           int bb=normal.el[2]< 0 ? 1 : 0;
           if(bb!=0)
        	   flag=true;
             
           int dim=P[0].length;
           if (flag) {
             for (int m = 0; m < n; m++) {
            	 if(dim==3)
               flag = (flag) && ((P[m].el[0] - P[i].el[0]) * normal.el[0] + (P[m].el[1] - P[i].el[1]) * normal.el[1] + (P[m].el[2] - P[i].el[2]) * normal.el[2] <= 0);
            	 else if(dim==2)
            		 flag = (flag) && ((P[m].el[0] - P[i].el[0]) * normal.el[0] + (P[m].el[1] - P[i].el[1]) * normal.el[1] <= 0);
             }

           }

           if (!flag)
           {
             continue;
           }
           

          result.add(new GraphEdge(i,j));

          result.add(new GraphEdge(j,k));
        
          result.add(new GraphEdge(k,i));

           this.adjMatrix[i][j] = 1;
           this.adjMatrix[j][i] = 1;
           this.adjMatrix[k][i] = 1;
           this.adjMatrix[i][k] = 1;
           this.adjMatrix[j][k] = 1;
           this.adjMatrix[k][j] = 1;
         }

       }

     }
     
     return result;
   }

/*   public TreeSet<GraphEdge> getEdges(TreeSet<?> pointsSet)
   {
     if ((pointsSet != null) && (pointsSet.size() > 0))
     {
       int n = pointsSet.size();

       int[] x = new int[n];
       int[] y = new int[n];
       int[] z = new int[n];

       int i = 0;

       Iterator<?> iterator = pointsSet.iterator();
       while (iterator.hasNext())
       {
         Point point = (Point)iterator.next();

         x[i] = (int)point.getX();
         y[i] = (int)point.getY();
         z[i] = (x[i] * x[i] + y[i] * y[i]);

         i++;
       }

       return getEdges();
     }

     return null;
   }*/
   
   

   
   private class GraphEdge implements Comparable<Object> {
	   Integer n1, n2;
	   private GraphEdge(int n1, int n2){
		   if(n1<n2) { this.n1=n1;this.n2=n2;}
		   else{ this.n1=n2;this.n2=n1;}
		
	   }
	   @Override

	public int compareTo(Object o) {
		   
		   int b=1;
		   GraphEdge other = (GraphEdge) o;
	
		 
		if (n1==other.n1)
			if(n2==other.n2) 
			{
				b=0;
			util.pr(b);
			}
		
		  util.pr("-------");
		return b;
	}

   }
   
   
 }

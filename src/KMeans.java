
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.*;

import javax.imageio.ImageIO;
 

public class KMeans {
	
	static int[] rgb=new int[1024*768];
	static int[] r=new int[1024*768];
	static int[] g=new int[1024*768];
	static int[] b=new int[1024*768];
	
	
    public static void main(String [] args){
	if (args.length < 3){
	    System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
	    return;
	}
	try{
	    BufferedImage originalImage = ImageIO.read(new File(args[0]));
	    int k=Integer.parseInt(args[1]);
	    BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
	    System.out.println("Printing Image.....");
	    ImageIO.write(kmeansJpg, "jpg", new File(args[2])); 
	    
	}catch(IOException e){
	    System.out.println(e.getMessage());
	}	
    }
    
    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k){
	int w=originalImage.getWidth();
	int h=originalImage.getHeight();
	BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
	Graphics2D g1 = kmeansImage.createGraphics();
	g1.drawImage(originalImage, 0, 0, w,h , null);
	// Read rgb values from the image
	
	
	
	int count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		rgb[count]=kmeansImage.getRGB(i,j);
		
	    count++;
	}}
	
	//Create K buckets or classes
	int[] kpoints= Selector(k,w,h);
	// Call kmeans algorithm: update the rgb values
	for(int i=0;i<50;i++)
		kpoints=kmeans(kpoints,k,w,h);
// update RGB array from R G and B
	int l=0;
	for(int i=0;i<w*h;i++)
	{
		rgb[i] = ((r[i]&0x0ff)<<16)|((g[i]&0x0ff)<<8)|(b[i]&0x0ff);
		System.out.println("Pixel number="+i+"    RGB value="+rgb[i]);
	}	
	
	// Write the new rgb values to the image
	count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		kmeansImage.setRGB(i,j,rgb[count++]);
	    }
	}
	System.out.println("Returning Object");
	return kmeansImage;
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static int[] kmeans(int[] kpoints, int k,int w, int h)
    
    {
		int[] newk=new int[kpoints.length];
		newk=kpoints;
    	
		
		for(int count=0; count<w*h;count++)
		{
		r[count]=(rgb[count]>>16) & 0xff; 
		g[count]=(rgb[count]>>8) & 0xff;
		b[count]=(rgb[count]) & 0xff;
		}
    
		// Calculating 3d distances
		double[][] distances= new double[k][w*h];
		for(int i=0;i<w*h;i++)
			for(int j=0;j<k;j++)
			{	
			distances[j][i]=(double)Math.sqrt(Math.abs((r[kpoints[j]]-r[i])^2+(g[kpoints[j]]-g[i])^2+(b[kpoints[j]]-b[i])^2));
			//System.out.println(distances[j][i]);
			}
		
		int[] answer=FindLeastDist(distances,w,h,k);
		int ti=answer[1];
		int tj=answer[2];
		int temp=0;
		temp=kpoints[tj];
		kpoints[tj]=ti;
		r[temp]=r[ti+100];
		g[temp]=g[ti+100];
		b[temp]=b[ti+100];
		//r[temp]=0;
		//g[temp]=0;
		//b[temp]=0;
		
		//System.out.println(Arrays.toString(kpoints));		
		return kpoints;
    }
   	
    private static int[] Selector(int k, int w,int h)
    {
    	// Select K points
    			int i1=0;
    			int[] kpoints=new int[k];
    			while(i1<k)
    			{	
    				Random rand=new Random();
    				int min=0;
    				int max=w*h;
    				kpoints[i1] = rand.nextInt(max - min + 1) + min;
    				i1++;	
    			}
    	return kpoints;
    }
    private static int[] FindLeastDist(double[][] distances,int w,int h,int k)
    { 	int ti=0,tj=0;
    	double temp = 100000; 
    	for(int i=0;i<w*h;i++)
			for(int j=0;j<k;j++)
			{	
				if((int)distances[j][i]!=0&&distances[j][i]<temp)
			{temp=distances[j][i];
			ti=i;
			tj=j;
			//System.out.println(temp+"     j="+j+"     i="+i);
			}
			}
    	int[] answer=new int[3];
    	answer[0]=(int)temp; answer[1]=ti; answer[2]=tj;
    	return answer;
    }
}

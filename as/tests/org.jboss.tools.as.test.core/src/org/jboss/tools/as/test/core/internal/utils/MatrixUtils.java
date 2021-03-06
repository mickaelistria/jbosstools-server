package org.jboss.tools.as.test.core.internal.utils;

import java.util.ArrayList;

public class MatrixUtils {
	public static Object[] createPath(Object[][] blocks, int[] vals) {
		Object[] ret = new Object[blocks.length];
		for( int i = 0; i < blocks.length; i++ ) {
			ret[i] = blocks[i][vals[i]];
		}
		return ret;
	}
	
	
	/**
	 * Accept an array of the following style:
	 *    Object[][] { 
	 *       new Object[] { "server1, server2"},
	 *       new Object[] { "zipped", "unzipped"}
	 *    };
	 * and return values of:
	 *    Object[][] {
	 *       new Object[] {"server1", "zipped"},
	 *       new Object[] {"server1", "unzipped"},
	 *       new Object[] {"server2", "zipped"},
	 *       new Object[] {"server2", "unzipped"}
	 *    }
	 * @param params
	 * @return
	 */
	public static ArrayList<Object[]> toMatrix(Object[][] blocks) {
		ArrayList<Object[]> paths = new ArrayList<Object[]>();
    	int depth = blocks.length;
    	int[] depthCount = new int[depth];
    	for( int i = 0; i < depth; i++ ) {
    		depthCount[i] = 0;
    	}
    	boolean done = false;
    	while(!done ) {
    		paths.add(createPath(blocks, depthCount));
    		depthCount[depth-1]++;
    		for( int i = depth-1; i > 0; i-- ) {
    			if(depthCount[i] == blocks[i].length) {
    				depthCount[i] = 0;
    				depthCount[i-1]++;
    			} 
    		}
    		if( depthCount[0] == blocks[0].length)
    			done = true;
    	}
    	return paths;
	}
	
	

}

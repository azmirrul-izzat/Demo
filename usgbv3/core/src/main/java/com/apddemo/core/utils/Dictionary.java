package com.apddemo.core.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.apddemo.core.utils.Dictionary.Bucket.Node; 

public class Dictionary {
    private int M = 1319; //prime number
    final private Bucket[] array;
    public Dictionary() {
        this.M = M;

        array = new Bucket[M];
        for (int i = 0; i < M; i++) {
            array[i] = new Bucket();
        }
    }

    private int hash(String key) {
        return (key.hashCode() & 0x7fffffff) % M;
    }

    //call hash() to decide which bucket to put it in, do it.
    public void add(String key) {
        array[hash(key)].put(key);
    }

    //call hash() to find what bucket it's in, get it from that bucket. 
    public boolean contains(String input) {
        input = input.toLowerCase();
        return array[hash(input)].get(input);
    }

    public void build(ResourceResolver resourceResolver, String filePath) {
        try {
//            BufferedReader reader = new BufferedReader(new FileReader(filePath));
        	Resource resource = resourceResolver.getResource(filePath);
        	javax.jcr.Node asset = resource.adaptTo(javax.jcr.Node.class);
        	BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(asset.getNode("jcr:content").getProperty("jcr:data").getStream()));
			} catch (ValueFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PathNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            String line;
            while ((line = reader.readLine()) != null) {
                add(line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }
    //this method is used in my unit tests
    public String[] getRandomEntries(int num){
        String[] toRet = new String[num];
        for (int i = 0; i < num; i++){
            //pick a random bucket, go out a random number 
            Node n = array[(int)Math.random()*M].first;
            int rand = (int)Math.random()*(int)Math.sqrt(num);

            for(int j = 0; j<rand && n.next!= null; j++) n = n.next;
            toRet[i]=n.word;


        }
        return toRet;
    }

    class Bucket {

        private Node first;

        public boolean get(String in) {         //return key true if key exists
            Node next = first;
            while (next != null) {
                if (next.word.equals(in)) {
                    return true;
                }
                next = next.next;
            }
            return false;
        }

        public void put(String key) {
            for (Node curr = first; curr != null; curr = curr.next) {
                if (key.equals(curr.word)) {
                    return;                     //search hit: return
                }
            }
            first = new Node(key, first); //search miss: add new node
        }

        class Node {

            String word;
            Node next;

            public Node(String key, Node next) {
                this.word = key;
                this.next = next;
            }

        }

    }
}
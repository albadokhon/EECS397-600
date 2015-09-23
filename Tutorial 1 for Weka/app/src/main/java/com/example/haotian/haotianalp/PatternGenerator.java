/*
Copyright 2010-2013 Michael Shick

This file is part of 'Lock Pattern Generator'.

'Lock Pattern Generator' is free software: you can redistribute it and/or
modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or (at your option)
any later version.

'Lock Pattern Generator' is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details.

You should have received a copy of the GNU General Public License along with
'Lock Pattern Generator'.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.example.haotian.haotianalp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PatternGenerator
{
    protected int mGridLength;
    protected int mMinNodes;
    protected int mMaxNodes;
    protected int count = 0; //CB Keep count of how many times the generate pattern button is clicked
    protected Random mRng;
    protected List<Point> mAllNodes;

    public PatternGenerator()
    {
        mRng = new Random();
        setGridLength(0);
        setMinNodes(0);
        setMaxNodes(0);
    }

    public List<Point> getPattern()
    {
      //  List<Point> temp = mAllNodes; //AB just to not modify a global variable
      //  int seqcount = getMaxNodes() - mRng.nextInt(getMinNodes()); //AB CB HW1 this will return the count sequence possibilities (5 or 4 or 3)
        List<Point> pattern = new ArrayList<Point>(); //This is the final Array which will be returned from this function

        //CB A2 Lock pattern #1
        if (count == 0) {
            pattern.add(new Point(0,1));
            pattern.add(new Point(1,2));
            pattern.add(new Point(2,1));
            pattern.add(new Point(1,0));
            count++; // CB adds one onto the count to indicate the button has been pressed
        }

        //CB A2 Lock pattern #2
        else if (count == 1) { //CB Lock pattern #2
            pattern.add(new Point(0,0));
            pattern.add(new Point(1,0));
            pattern.add(new Point(2,0));
            pattern.add(new Point(1,1));
            pattern.add(new Point(0,2));
            count++; // CB A2 adds one onto the count to indicate the button has been pressed
        }

        //CB A2 Lock pattern #3
        else if (count == 2) {
            pattern.add(new Point(0, 1));
            pattern.add(new Point(1, 1));
            pattern.add(new Point(2, 1));
            pattern.add(new Point(2, 0));
            pattern.add(new Point(1, 0));
            pattern.add(new Point(0,0));
            count++; //CB A2 adds one to the count to indicate the button has been pressed
        }

        //CB A2 Lock pattern #4
        else if (count == 3) {
            pattern.add(new Point(1,0));
            pattern.add(new Point(1,1));
            pattern.add(new Point(0,2));
            pattern.add(new Point(0,1));
            count = 0; //CB A2 resets the counter to 0
        }
        //AB Debug
        //pattern.add(new Point(0,0));
        //pattern.add(new Point(0,1));
        //pattern.add(new Point(0,2));


      /*  for (int i = 0; i<seqcount; i++) {
            int randxyidx = mRng.nextInt(temp.size() - 1); //AB CB HW1 this will generate an index number depending on the remainder of all possibilities
            Point randxy = temp.get(randxyidx); //AB CB HW1 get that index point

            if (i==0) {
                pattern.add(randxy); //AB CB HW1 Assume this point is correct and it will be moved from m
                temp.remove(randxy); //AB CB HW1 it searches for the point given and removes it from the array.
            }
            else {
                Point old = pattern.get(pattern.size() - 1);
                List<Point> temp1 = temp; //AB CB HW1 will be used in the next for loop for internal use only.
                int deltax = Math.abs(randxy.x - old.x);
                int deltay = Math.abs(randxy.y - old.y);


                //AB CB HW1 better than an infinite while(), get out when j=temp1.size()
                //temp1 will be cloned before this loop and all randomly chosen indecies will be removed
                //from temp1 if they don't qualify as a new point...
                for (int j = 0; j<temp1.size(); j++) {
                    deltax = Math.abs(randxy.x - old.x);
                    deltay = Math.abs(randxy.y - old.y);
                    if (pattern.contains(randxy)) { //AB a loop to always choose a value that is not included in the pattern
                        temp1.remove(randxy);
                        randxyidx = mRng.nextInt(temp1.size()-1); //AB this will generate an index number.
                        randxy = temp1.get(randxyidx);
                    }

                    //AB CB HW1 This checking for the new point has to be in that order so it will go through
                    //AB CB HW1 all conditions in this sequence ending with else{}.
                    //AB CB HW1 else if new.x == old.x then { if |new.y-old.y| >1 : repeat random;-- else : add to array, accept this pattern}
                    else if (old.x == randxy.x) {
                        if (deltay > 1) {
                            temp1.remove(randxy);
                            randxyidx = mRng.nextInt(temp1.size()-1); //AB this will generate an index number.
                            randxy = temp1.get(randxyidx);
                        } else {
                            pattern.add(randxy);
                            temp.remove(randxy);
                            j=temp1.size();
                        }
                    }
                    //AB CB HW1 else if new.y == old.y then { if |new.x-old.x| >1 : repeat random;-- else : add to array, accept this pattern}
                    else if (old.y == randxy.y) {
                        if (deltax > 1) {
                            temp1.remove(randxy);
                            randxyidx = mRng.nextInt(temp1.size()-1); //AB this will generate an index number.
                            randxy = temp1.get(randxyidx);
                        } else {
                            pattern.add(randxy);
                            temp.remove(randxy);
                            j=temp1.size();
                        }
                    }
                    //AB CB HW1 else if |new.x-old.x| > 1 && |new.y-old.y| > 1 :repeat random; unwanted pattern
                    else if ((deltax > 1) && (deltay > 1)) {
                        temp1.remove(randxy);
                        randxyidx = mRng.nextInt(temp1.size()-1); //AB this will generate an index number.
                        randxy = temp1.get(randxyidx);
                    }

                    //AB CB HW1 else { Add whatever falls out of this algorithm to the Array, that is for sure it will be OK :)
                    else {
                        pattern.add(randxy);
                        temp.remove(randxy);
                        j=temp1.size();
                    }
                }
            }
        } */
        return pattern;
    }

    //
    // Accessors / Mutators
    //

    public void setGridLength(int length)
    {
        // build the prototype set to copy from later
        List<Point> allNodes = new ArrayList<Point>();
        for(int y = 0; y < length; y++)
        {
            for(int x = 0; x < length; x++)
            {
                allNodes.add(new Point(x,y));
            }
        }
        mAllNodes = allNodes;

        mGridLength = length;
    }
    public int getGridLength()
    {
        return mGridLength;
    }

    public void setMinNodes(int nodes)
    {
        mMinNodes = nodes;
    }
    public int getMinNodes()
    {
        return mMinNodes;
    }

    public void setMaxNodes(int nodes)
    {
        mMaxNodes = nodes;
    }
    public int getMaxNodes()
    {
        return mMaxNodes;
    }

    //
    // Helper methods
    //

    public static int computeGcd(int a, int b)
    /* Implementation taken from
     * http://en.literateprograms.org/Euclidean_algorithm_(Java)
     * Accessed on 12/28/10
     */
    {
        if(b > a)
        {
            int temp = a;
            a = b;
            b = temp;
        }

        while(b != 0)
        {
            int m = a % b;
            a = b;
            b = m;
        }

        return a;
    }
}

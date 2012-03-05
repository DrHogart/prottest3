/*
Copyright (C) 2009  Diego Darriba

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package es.uvigo.darwin.prottest.tree;

import java.util.Hashtable;

import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

import pal.tree.Tree;

/**
 * Caches the distances between every couple of trees in a set. This is
 * useful for instance when a tree-distance-based algorithm is computed
 * many times, like the Decision Theory Information Criterion.
 * 
 * This cache support both euclidean and RF distances.
 * 
 * @author Diego Darriba López
 * 
 * @since 3.0
 */
public class TreeDistancesCache {

    /** Constant of the euclidean distances */
    public static final int EUCLIDEAN = 1;
    /** Constant of the RF distances */
    public static final int ROBINSON_FOULDS = 2;
    /** Cache hash table coupling each pair of trees with its distance */
    private Hashtable<TreePair, Double> distances;
    /** Sort of computed distance */
    private int distanceType;

    /**
     * Gets the distance type.
     * 
     * @return the sort of distance, according to the defined constants
     */
    public int getDistanceType() {
        return distanceType;
    }

    /**
     * Instantiates a new tree distances cache.
     * 
     * @param distanceType sort of distance to compute
     */
    public TreeDistancesCache(int distanceType) {

        if (distanceType != EUCLIDEAN && distanceType != ROBINSON_FOULDS) {
            throw new ProtTestInternalException("Unsupported distance type");
        }
        this.distanceType = distanceType;
        distances = new Hashtable<TreePair, Double>();

    }

    /**
     * Gets the distance between two trees
     * 
     * @param t1 the first tree
     * @param t2 the second tree
     * 
     * @return the distance
     */
    public double getDistance(Tree t1, Tree t2) {
        double distance = 0.0;
        TreePair tp = new TreePair(t1, t2);
        if (distances.containsKey(tp)) {
            distance = distances.get(tp);
        } else {
            switch (distanceType) {
                case EUCLIDEAN:
                    distance = TreeUtils.euclideanTreeDistance(t1, t2);
                    break;
                case ROBINSON_FOULDS:
                    distance = TreeUtils.robinsonFouldsTreeDistance(t1, t2);
                    break;
            }
            distances.put(tp, distance);
        }
        return distance;
    }

    /**
     * This class represents a pair of trees
     */
    private class TreePair {

        private Tree t1,  t2;

        public TreePair(Tree t1, Tree t2) {
            this.t1 = t1;
            this.t2 = t2;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((t1 == null) ? 0 : t1.hashCode());
            result = prime * result + ((t2 == null) ? 0 : t2.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            TreePair other = (TreePair) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (t1 == null) {
                if (other.t1 != null) {
                    return false;
                }
            } else if (!t1.equals(other.t1)) {
                return false;
            }
            if (t2 == null) {
                if (other.t2 != null) {
                    return false;
                }
            } else if (!t2.equals(other.t2)) {
                return false;
            }
            return true;
        }

        private TreeDistancesCache getOuterType() {
            return TreeDistancesCache.this;
        }
    }
}

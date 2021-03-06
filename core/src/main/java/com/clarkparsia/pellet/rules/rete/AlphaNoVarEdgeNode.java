// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.Iterator;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.rules.model.AtomConstant;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.BinaryAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.rete.WME.EdgeDirection;

/**
 */
public class AlphaNoVarEdgeNode extends AlphaFixedEdgeNode {
	private final ATermAppl objectName;
	private Node objectNode;
	
	public AlphaNoVarEdgeNode(ABox abox, Role role, ATermAppl subjectName, ATermAppl objectName) {
	    super(abox, role, subjectName);
	    
	    this.objectName = objectName;
    }
	

	protected Node initObjectNode() {
		if (objectNode == null) {			
			objectNode = initNode(objectName);
		}
		assert objectNode != null;
		return objectNode;
	}
	
	public boolean activate(Edge edge) {
		Individual subject = initNode();
		Node object = initObjectNode();
		EdgeDirection dir = edgeMatches(edge);
		if (dir != null && (dir == EdgeDirection.FORWARD ? edge.getFrom() : edge.getTo()).isSame(subject) && (dir == EdgeDirection.BACKWARD ? edge.getFrom() : edge.getTo()).isSame(object)) {
			activate(WME.createEdge(edge, dir));
			return true;
		}
		return false;
	}

	public Iterator<WME> getMatches(int argIndex, Node arg) {
		throw new UnsupportedOperationException();
	}

	public Iterator<WME> getMatches() {
		Individual subject = initNode();
		Node object = initObjectNode();
		EdgeList edges = subject.getEdgesTo(object, role);
		return toWMEs(edges, EdgeDirection.FORWARD);
	}

	public boolean matches(RuleAtom atom) {
		return ((atom instanceof IndividualPropertyAtom) || (atom instanceof DatavaluedPropertyAtom)) 
						&& atom.getPredicate().equals(role.getName())
						&& ((BinaryAtom) atom).getArgument1() instanceof AtomIConstant
						&& ((AtomIConstant)((BinaryAtom) atom).getArgument1()).getValue().equals(name)
						&& ((BinaryAtom) atom).getArgument2() instanceof AtomConstant
						&& ((AtomConstant)((BinaryAtom) atom).getArgument2()).getValue().equals(objectName);
	}
	
	public String toString() {
		return ATermUtils.toString(role.getName()) + "(" + ATermUtils.toString(name) + ", " + ATermUtils.toString(objectName) + ")";
	}
}

package hu.bme.mit.inf.ttmc.program.tcfa.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import hu.bme.mit.inf.ttmc.program.tcfa.TCFA;

public class MutableTCFAImpl implements TCFA {

	private MutableTCFALocImpl initLoc;
	private MutableTCFALocImpl finalLoc;
	private MutableTCFALocImpl errorLoc;
	
	private final Collection<MutableTCFALocImpl> locs;
	private final Collection<MutableTCFAEdgeImpl> edges;
	
	public MutableTCFAImpl() {
		initLoc = createLoc();
		finalLoc = createLoc();
		errorLoc = createLoc();
		
		locs = new HashSet<>();
		edges = new HashSet<>();
	}
	
	////
	
	@Override
	public MutableTCFALocImpl getInitLoc() {
		return initLoc;
	}
	
	public void setInitLoc(final MutableTCFALocImpl initLoc) {
		checkNotNull(initLoc);
		checkArgument(initLoc.getTCFA() == this);
		this.initLoc = initLoc;
	}
	
	////
	
	@Override
	public MutableTCFALocImpl getFinalLoc() {
		return finalLoc;
	}
	
	
	public void setFinalLoc(final MutableTCFALocImpl finalLoc) {
		checkNotNull(finalLoc);
		checkArgument(finalLoc.getTCFA() == this);
		this.finalLoc = finalLoc;
	}
	
	////
	
	@Override
	public MutableTCFALocImpl getErrorLoc() {
		return errorLoc;
	}
	
	public void setErrorLoc(final MutableTCFALocImpl errorLoc) {
		checkNotNull(errorLoc);
		checkArgument(errorLoc.getTCFA() == this);
		this.errorLoc = errorLoc;
	}
	
	////
	
	@Override
	public Collection<MutableTCFALocImpl> getLocs() {
		return Collections.unmodifiableCollection(locs);
	}
	
	public MutableTCFALocImpl createLoc() {
		final MutableTCFALocImpl loc = new MutableTCFALocImpl(this);
		locs.add(loc);
		return loc;
	}
	
	public void deleteLoc(final MutableTCFALocImpl loc) {
		checkNotNull(loc);
		checkArgument(loc.getTCFA() == this);
		checkArgument(loc != initLoc);
		checkArgument(loc != finalLoc);
		checkArgument(loc != errorLoc);
		checkArgument(loc.getInEdges().isEmpty());
		checkArgument(loc.getOutEdges().isEmpty());
		loc.unsetTCFA();
		locs.remove(loc);
	}
	
	////
	
	@Override
	public Collection<MutableTCFAEdgeImpl> getEdges() {
		return Collections.unmodifiableCollection(edges);
	}
	
	public MutableTCFAEdgeImpl createEdge(final MutableTCFALocImpl source, final MutableTCFALocImpl target) {
		checkNotNull(source);
		checkNotNull(target);
		checkArgument(source.getTCFA() == this);
		checkArgument(target.getTCFA() == this);
		
		final MutableTCFAEdgeImpl edge = new MutableTCFAEdgeImpl(this, source, target);
		source.addOutEdge(edge);
		target.addOutEdge(edge);
		edges.add(edge);
		return edge;
	}
	
	public void deleteEdge(final MutableTCFAEdgeImpl edge) {
		checkNotNull(edge);
		checkArgument(edge.getTCFA() == this);
		
		edge.getSource().removeOutEdge(edge);
		edge.getTarget().removeInEdge(edge);
		edge.unsetTCFA();
		edges.remove(edge);
	}
	
}

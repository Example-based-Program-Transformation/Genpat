/**
 * Copyright (C) SEI, PKU, PRC. - All Rights Reserved.
 * Unauthorized copying of this file via any medium is
 * strictly prohibited Proprietary and Confidential.
 * Written by Jiajun Jiang<jiajun.jiang@pku.edu.cn>.
 */
package mfix.core.node.ast.expr;

import mfix.core.node.ast.Node;
import mfix.core.node.match.metric.FVector;
import mfix.core.node.modify.Update;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author: Jiajun
 * @date: 2018/9/21
 */
public class VarDeclarationExpr extends Expr {

	private static final long serialVersionUID = -5908284718888454712L;
	private MType _declType = null;
	private List<Vdf> _vdfs = null;
	
	
	/**
	 * VariableDeclarationExpression:
     *	{ ExtendedModifier } Type VariableDeclarationFragment
     *	    { , VariableDeclarationFragment }
	 */
	public VarDeclarationExpr(String fileName, int startLine, int endLine, ASTNode node) {
		super(fileName, startLine, endLine, node);
		_nodeType = TYPE.VARDECLEXPR;
	}

	public void setDeclType(MType declType) {
		_declType = declType;
	}

	public void setVarDeclFrags(List<Vdf> vdfs) {
		_vdfs = vdfs;
	}

	public MType getDeclType() {
		return _declType;
	}

	public List<Vdf> getFragments() {
		return _vdfs;
	}

	@Override
	public StringBuffer toSrcString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(_declType.toSrcString());
		stringBuffer.append(" ");
		stringBuffer.append(_vdfs.get(0).toSrcString());
		for (int i = 1; i < _vdfs.size(); i++) {
			stringBuffer.append(",");
			stringBuffer.append(_vdfs.get(i).toSrcString());
		}
		return stringBuffer;
	}

	@Override
	protected void tokenize() {
		_tokens = new LinkedList<>();
		_tokens.addAll(_declType.tokens());
		_tokens.addAll(_vdfs.get(0).tokens());
		for (int i = 1; i < _vdfs.size(); i++) {
			_tokens.add(",");
			_tokens.addAll(_vdfs.get(i).tokens());
		}
	}

	@Override
	public boolean compare(Node other) {
		boolean match = false;
		if (other instanceof VarDeclarationExpr) {
			VarDeclarationExpr varDeclarationExpr = (VarDeclarationExpr) other;
			match = _declType.compare(varDeclarationExpr._declType);
			match = match && (_vdfs.size() == varDeclarationExpr._vdfs.size());
			for (int i = 0; match && i < _vdfs.size(); i++) {
				match = match && _vdfs.get(i).compare(varDeclarationExpr._vdfs.get(i));
			}
		}
		return match;
	}

	@Override
	public List<Node> getAllChildren() {
		List<Node> children = new ArrayList<>(_vdfs.size() + 1);
		children.add(_declType);
		children.addAll(_vdfs);
		return children;
	}

	@Override
	public void computeFeatureVector() {
		_fVector = new FVector();
		_fVector.combineFeature(_declType.getFeatureVector());
		for (Vdf vdf : _vdfs) {
			_fVector.combineFeature(vdf.getFeatureVector());
		}
	}

	@Override
	public boolean postAccurateMatch(Node node) {
		VarDeclarationExpr vde = null;
		boolean match = false;
		if (getBindingNode() != null) {
			vde = (VarDeclarationExpr) getBindingNode();
			match = (vde == node);
		} else if (canBinding(node)) {
			vde = (VarDeclarationExpr) node;
			setBindingNode(node);
			match = true;
		}
		if (vde == null) {
			continueTopDownMatchNull();
		} else {
			_declType.postAccurateMatch(vde.getDeclType());
			greedyMatchListNode(getFragments(), vde.getFragments());
		}
		return match;
	}

	@Override
	public boolean genModidications() {
		if (super.genModidications()) {
			VarDeclarationExpr vde = (VarDeclarationExpr) getBindingNode();
			if (!_declType.compare(vde.getDeclType())) {
				Update update = new Update(this, _declType, vde.getDeclType());
				_modifications.add(update);
			}
			genModificationList(_vdfs, vde.getFragments(),false);
		}
		return true;
	}

	@Override
	public boolean ifMatch(Node node, Map<Node, Node> matchedNode, Map<String, String> matchedStrings) {
		if(node instanceof VarDeclarationExpr) {
			return checkDependency(node, matchedNode, matchedStrings)
					&& matchSameNodeType(node, matchedNode, matchedStrings);
		} else {
			return false;
		}
	}
}
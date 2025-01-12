/**
 * Copyright (C) SEI, PKU, PRC. - All Rights Reserved.
 * Unauthorized copying of this file via any medium is
 * strictly prohibited Proprietary and Confidential.
 * Written by Jiajun Jiang<jiajun.jiang@pku.edu.cn>.
 */
package mfix.core.node.ast.expr;

import mfix.core.node.match.MatchLevel;
import mfix.core.node.ast.Node;
import mfix.core.pattern.cluster.VIndex;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;

import java.util.LinkedList;
import java.util.Map;

/**
 * @author: Jiajun
 * @date: 2018/9/21
 */
public class AssignOperator extends Operator {

    private static final long serialVersionUID = 2573726544838821670L;
    private String _operatorStr;
    private transient Assignment.Operator _operator;

    public AssignOperator(String fileName, int startLine, int endLine, ASTNode oriNode) {
        super(fileName, startLine, endLine, oriNode);
        _nodeType = TYPE.ASSIGNOPERATOR;
        _fIndex = VIndex.EXP_ASSIGN_OP;
    }

    public void setOperator(Assignment.Operator operator) {
        this._operator = operator;
        this._operatorStr = operator.toString();
    }

    public Assignment.Operator getOperator() {
        return _operator;
    }

    public String getOperatorStr() {
        return _operatorStr;
    }

    @Override
    public boolean compare(Node other) {
        if (other != null && other instanceof AssignOperator) {
            return _operatorStr.equals(((AssignOperator) other)._operatorStr);
        }
        return false;
    }

    @Override
    public StringBuffer toSrcString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(_operatorStr);
        return stringBuffer;
    }

    @Override
    protected void tokenize() {
        _tokens = new LinkedList<>();
        _tokens.add(_operatorStr);
    }

    @Override
    public boolean postAccurateMatch(Node node) {
        if (getBindingNode() == node) return true;
        if (getBindingNode() == null && canBinding(node)) {
            setBindingNode(node);
            return true;
        }
        return false;
    }

    @Override
    public boolean ifMatch(Node node, Map<Node, Node> matchedNode, Map<String, String> matchedStrings, MatchLevel level) {
        if (node instanceof AssignOperator){
            if (!isChanged() || _operatorStr.equals(node.toSrcString().toString())) {
                matchedNode.put(this, node);
                return true;
            }
        }
        return false;
    }
}

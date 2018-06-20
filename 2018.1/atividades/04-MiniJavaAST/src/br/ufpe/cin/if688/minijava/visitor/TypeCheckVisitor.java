package br.ufpe.cin.if688.minijava.visitor;

import br.ufpe.cin.if688.minijava.ast.And;
import br.ufpe.cin.if688.minijava.ast.ArrayAssign;
import br.ufpe.cin.if688.minijava.ast.ArrayLength;
import br.ufpe.cin.if688.minijava.ast.ArrayLookup;
import br.ufpe.cin.if688.minijava.ast.Assign;
import br.ufpe.cin.if688.minijava.ast.Block;
import br.ufpe.cin.if688.minijava.ast.BooleanType;
import br.ufpe.cin.if688.minijava.ast.Call;
import br.ufpe.cin.if688.minijava.ast.ClassDeclExtends;
import br.ufpe.cin.if688.minijava.ast.ClassDeclSimple;
import br.ufpe.cin.if688.minijava.ast.False;
import br.ufpe.cin.if688.minijava.ast.Formal;
import br.ufpe.cin.if688.minijava.ast.Identifier;
import br.ufpe.cin.if688.minijava.ast.IdentifierExp;
import br.ufpe.cin.if688.minijava.ast.IdentifierType;
import br.ufpe.cin.if688.minijava.ast.If;
import br.ufpe.cin.if688.minijava.ast.IntArrayType;
import br.ufpe.cin.if688.minijava.ast.IntegerLiteral;
import br.ufpe.cin.if688.minijava.ast.IntegerType;
import br.ufpe.cin.if688.minijava.ast.LessThan;
import br.ufpe.cin.if688.minijava.ast.MainClass;
import br.ufpe.cin.if688.minijava.ast.MethodDecl;
import br.ufpe.cin.if688.minijava.ast.Minus;
import br.ufpe.cin.if688.minijava.ast.NewArray;
import br.ufpe.cin.if688.minijava.ast.NewObject;
import br.ufpe.cin.if688.minijava.ast.Not;
import br.ufpe.cin.if688.minijava.ast.Plus;
import br.ufpe.cin.if688.minijava.ast.Print;
import br.ufpe.cin.if688.minijava.ast.Program;
import br.ufpe.cin.if688.minijava.ast.This;
import br.ufpe.cin.if688.minijava.ast.Times;
import br.ufpe.cin.if688.minijava.ast.True;
import br.ufpe.cin.if688.minijava.ast.Type;
import br.ufpe.cin.if688.minijava.ast.VarDecl;
import br.ufpe.cin.if688.minijava.ast.While;
import br.ufpe.cin.if688.minijava.symboltable.Method;
import br.ufpe.cin.if688.minijava.symboltable.SymbolTable;
import br.ufpe.cin.if688.minijava.symboltable.Class;

public class TypeCheckVisitor implements IVisitor<Type> {

	private SymbolTable symbolTable;
	private Class currClass;
	private Class currParent;
	private Method currMethod;
	TypeCheckVisitor(SymbolTable st) {
		symbolTable = st;
	}

	// MainClass m;
	// ClassDeclList cl;
	public Type visit(Program n) {
		n.m.accept(this);
		for (int i = 0; i < n.cl.size(); i++) {
			n.cl.elementAt(i).accept(this);
		}
		return null;
	}

	// Identifier i1,i2;
	// Statement s;
	public Type visit(MainClass n) {
		this.currClass = this.symbolTable.getClass(n.i1.toString());
		this.currMethod = this.symbolTable.getMethod("main", this.currClass.getId());
		n.i1.accept(this);
		n.i2.accept(this);
		n.s.accept(this);
		this.currClass = null;
		this.currMethod = null;
		return null;
	}

	// Identifier i;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Type visit(ClassDeclSimple n) {
		this.currClass = this.symbolTable.getClass(n.i.toString());
		n.i.accept(this);
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.elementAt(i).accept(this);
		}
		this.currClass = null;
		return null;
	}

	// Identifier i;
	// Identifier j;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Type visit(ClassDeclExtends n) {
		this.currClass = this.symbolTable.getClass(n.i.toString());
		this.currParent = this.symbolTable.getClass(n.j.toString());
		n.i.accept(this);
		n.j.accept(this);
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.elementAt(i).accept(this);
		}
		this.currClass = null;
		this.currParent = null;
		return null;
	}

	// Type t;
	// Identifier i;
	public Type visit(VarDecl n) {
		Type type = n.t.accept(this);
		n.i.accept(this);
		return type;
	}

	// Type t;
	// Identifier i;
	// FormalList fl;
	// VarDeclList vl;
	// StatementList sl;
	// Exp e;
	public Type visit(MethodDecl n) {
		this.currMethod = this.symbolTable.getMethod(n.i.toString(), this.currClass.getId());
		Type expectedType = n.t.accept(this);
		n.i.accept(this);
		for (int i = 0; i < n.fl.size(); i++) {
			n.fl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.elementAt(i).accept(this);
		}
		Type returned = n.e.accept(this);
		if (!this.symbolTable.compareTypes(expectedType, returned)) {
			System.out.println("This method expects other return type");
		}
		Type methodType = this.currMethod.type();
		this.currMethod = null;
		return methodType;
	}

	// Type t;
	// Identifier i;
	public Type visit(Formal n) {
		Type type = n.t.accept(this);
		n.i.accept(this);
		return type;
	}

	public Type visit(IntArrayType n) {
		return n;
	}

	public Type visit(BooleanType n) {
		return n;
	}

	public Type visit(IntegerType n) {
		return n;
	}

	// String s;
	public Type visit(IdentifierType n) {
		if (!this.symbolTable.containsClass(n.s)) {
			System.out.println("This identifier has never been declared");
			return null;
		}
		return n;
	}

	// StatementList sl;
	public Type visit(Block n) {
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.elementAt(i).accept(this);
		}
		return null;
	}

	// Exp e;
	// Statement s1,s2;
	public Type visit(If n) {
		Type expType = n.e.accept(this);
		if (!(expType instanceof BooleanType)) {
			System.out.println("This exp must be of boolean type");
			return null;
		}
		n.s1.accept(this);
		n.s2.accept(this);
		return null;
	}

	// Exp e;
	// Statement s;
	public Type visit(While n) {
		Type expType = n.e.accept(this);
		if (!(expType instanceof BooleanType)) {
			System.out.println("This exp must be of boolean type");
			return null;
		}
		n.s.accept(this);
		return null;
	}

	// Exp e;
	public Type visit(Print n) {
		n.e.accept(this);
		return null;
	}

	// Identifier i;
	// Exp e;
	public Type visit(Assign n) {
		Type idType = this.symbolTable.getVarType(this.currMethod, this.currClass, n.i.toString());
		n.i.accept(this);
		Type expType = n.e.accept(this);
		if (this.symbolTable.compareTypes(idType, expType)) {
			System.out.println("Invalid asignment");
			return null;
		}
		return null;
	}

	// Identifier i;
	// Exp e1,e2;
	public Type visit(ArrayAssign n) {
		Type arrayType = n.i.accept(this);
		Type exp1Type = n.e1.accept(this);
		Type exp2Type = n.e2.accept(this);
		if (!(arrayType instanceof IntArrayType)) {
			System.out.println("Array should be IntArrayType");
		}
		if (!(exp1Type instanceof IntegerType)) {
			System.out.println("Incompatible types");
		}
		if (!(exp2Type instanceof IntegerType)) {
			System.out.println("Incompatible types");
		}
		return null;
	}

	// Exp e1,e2;
	public Type visit(And n) {
		Type exp1 = n.e1.accept(this);
		Type exp2 = n.e2.accept(this);
		if (!(exp1 instanceof BooleanType) || !(exp2 instanceof BooleanType)) {
			System.out.println("Both sides of comparation must be of type Boolean");
		}
		return new BooleanType();
	}

	// Exp e1,e2;
	public Type visit(LessThan n) {
		Type exp1 = n.e1.accept(this);
		Type exp2 = n.e2.accept(this);
		if (!(exp1 instanceof IntegerType) || !(exp2 instanceof IntegerType)) {
			System.out.println("Both sides of comparation must be of Integer type");
		}
		return new BooleanType();
	}

	// Exp e1,e2;
	public Type visit(Plus n) {
		Type exp1 = n.e1.accept(this);
		Type exp2 = n.e2.accept(this);
		if (!(exp1 instanceof IntegerType) || !(exp2 instanceof IntegerType)) {
			System.out.println("Both sides of sum must be Integer");
		}
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(Minus n) {
		Type exp1 = n.e1.accept(this);
		Type exp2 = n.e2.accept(this);
		if (!(exp1 instanceof IntegerType) || !(exp2 instanceof IntegerType)) {
			System.out.println("Both sides of subtraction must be Integer");
		}
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(Times n) {
		Type exp1 = n.e1.accept(this);
		Type exp2 = n.e2.accept(this);
		if (!(exp1 instanceof IntegerType) || !(exp2 instanceof IntegerType)) {
			System.out.println("Both sides of multiplication must be Integer");
		}
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(ArrayLookup n) {
		Type exp1 = n.e1.accept(this);
		Type exp2 = n.e2.accept(this);
		if (!(exp1 instanceof IntArrayType)) {
			System.out.println("IntArrayType is required for arraylookup operation");
		}
		if (!(exp2 instanceof IntegerType)) {
			System.out.println("Integer is required to arraylookup index");
		}
		return new IntegerType();
	}

	// Exp e;
	public Type visit(ArrayLength n) {
		Type exp = n.e.accept(this);
		if (!(exp instanceof IntArrayType)) {
			System.out.println("IntArrayType is required for length operation");
		}
		return new IntegerType();
	}

	// Exp e;
	// Identifier i;
	// ExpList el;
	public Type visit(Call n) {
		n.e.accept(this);
		n.i.accept(this);
		for (int i = 0; i < n.el.size(); i++) {
			n.el.elementAt(i).accept(this);
		}
		return null;
	}

	// int i;
	public Type visit(IntegerLiteral n) {
		return new IntegerType();
	}

	public Type visit(True n) {
		return new BooleanType();
	}

	public Type visit(False n) {
		return new BooleanType();
	}

	// String s;
	public Type visit(IdentifierExp n) {
		Type idType = this.symbolTable.getVarType(this.currMethod, this.currClass, n.s);
		if (idType == null) {
			System.out.println("Symbol has not been declared");
		}
		return idType;
	}

	public Type visit(This n) {
		return this.currClass.type();
	}

	// Exp e;
	public Type visit(NewArray n) {
		Type exp = n.e.accept(this);
		if (!(exp instanceof IntegerType)) {
			System.out.println("Array must be of type integer");
		}
		return new IntArrayType();
	}

	// Identifier i;
	public Type visit(NewObject n) {
		n.i.accept(this);
		return this.symbolTable.getClass(n.i.toString()).type();
	}

	// Exp e;
	public Type visit(Not n) {
		Type exp = n.e.accept(this);
		if (!(exp instanceof BooleanType)) {
			System.out.println("Expression must be of type Boolean for not opperation");
		}
		return new BooleanType();
	}

	// String s;
	public Type visit(Identifier n) {
		return this.symbolTable.getMethodType(n.s);
	}
}

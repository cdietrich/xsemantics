package it.xsemantics.example.expressions.generator;

import com.google.inject.Inject;
import it.xsemantics.example.expressions.expressions.Expression;
import it.xsemantics.example.expressions.expressions.Model;
import it.xsemantics.example.expressions.expressions.Type;
import it.xsemantics.example.expressions.expressions.Variable;
import it.xsemantics.example.expressions.typing.ExpressionsSemantics;
import it.xsemantics.runtime.Result;
import it.xsemantics.runtime.RuleApplicationTrace;
import it.xsemantics.runtime.StringRepresentation;
import it.xsemantics.runtime.util.TraceUtils;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;

@SuppressWarnings("all")
public class ExpressionsGenerator implements IGenerator {
  @Inject
  private ExpressionsSemantics semantics;
  
  @Inject
  private TraceUtils _traceUtils;
  
  @Inject
  private StringRepresentation _stringRepresentation;
  
  public void doGenerate(final Resource resource, final IFileSystemAccess fsa) {
    URI _uRI = resource.getURI();
    String _lastSegment = _uRI.lastSegment();
    final int dotIndex = _lastSegment.lastIndexOf(".");
    URI _uRI_1 = resource.getURI();
    String _lastSegment_1 = _uRI_1.lastSegment();
    final String fileName = _lastSegment_1.substring(0, dotIndex);
    String _plus = (fileName + ".output");
    EList<EObject> _contents = resource.getContents();
    EObject _get = _contents.get(0);
    String _compileModel = this.compileModel(((Model) _get));
    fsa.generateFile(_plus, _compileModel);
  }
  
  public String compileModel(final Model expModel) {
    EList<Variable> _variables = expModel.getVariables();
    final Function1<Variable,CharSequence> _function = new Function1<Variable,CharSequence>() {
        public CharSequence apply(final Variable it) {
          CharSequence _compileVariable = ExpressionsGenerator.this.compileVariable(it);
          return _compileVariable;
        }
      };
    List<CharSequence> _map = ListExtensions.<Variable, CharSequence>map(_variables, _function);
    String _join = IterableExtensions.join(_map, "\n\n");
    return _join;
  }
  
  public CharSequence compileVariable(final Variable variable) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Variable: ");
    String _name = variable.getName();
    _builder.append(_name, "");
    _builder.newLineIfNotEmpty();
    Expression _expression = variable.getExpression();
    CharSequence _compileExpression = this.compileExpression(_expression);
    _builder.append(_compileExpression, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence compileExpression(final Expression exp) {
    CharSequence _xblockexpression = null;
    {
      RuleApplicationTrace _ruleApplicationTrace = new RuleApplicationTrace();
      final RuleApplicationTrace typeTrace = _ruleApplicationTrace;
      RuleApplicationTrace _ruleApplicationTrace_1 = new RuleApplicationTrace();
      final RuleApplicationTrace interpreterTrace = _ruleApplicationTrace_1;
      final Result<Type> type = this.semantics.type(null, typeTrace, exp);
      final Result<Object> result = this.semantics.interpret(null, interpreterTrace, exp);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("type: ");
      Type _value = type.getValue();
      String _string = this._stringRepresentation.string(_value);
      _builder.append(_string, "");
      _builder.newLineIfNotEmpty();
      _builder.append("type trace: ");
      String _traceAsString = this._traceUtils.traceAsString(typeTrace);
      _builder.append(_traceAsString, "");
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("interpretation: ");
      Object _value_1 = result.getValue();
      String _string_1 = this._stringRepresentation.string(_value_1);
      _builder.append(_string_1, "");
      _builder.newLineIfNotEmpty();
      _builder.append("interpretation trace: ");
      String _traceAsString_1 = this._traceUtils.traceAsString(interpreterTrace);
      _builder.append(_traceAsString_1, "");
      _builder.newLineIfNotEmpty();
      _xblockexpression = (_builder);
    }
    return _xblockexpression;
  }
}

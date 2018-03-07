package objectcomparator.repositories;

import java.util.List;
import java.util.Map;

import com.mendix.core.Core;
import com.mendix.core.objectmanagement.member.MendixObjectReference;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.systemwideinterfaces.core.IMendixObjectMember;

public class MendixObjectRepository {

	protected IContext context;
	
	public MendixObjectRepository(IContext context) {
		this.context = context;
	}
	
	public Map<String, ? extends IMendixObjectMember<?>> getMembers(IMendixObject object) {
		return (object.getMembers(this.context));
	}
	
	public Object getValue(IMendixObjectMember<?> member) {
		return (member.getValue(this.context));
	}
	
	public List<? extends MendixObjectReference> getReferences(IMendixObject object) {
		return (object.getReferences(this.context));
	}
	
	public List<IMendixObject> retrieveAssociatedObjects (IMendixObject object, String path) {
		return (Core.retrieveByPath(this.context, object, path));
	}
	
	public <T> String parseValueToString (IMendixObjectMember<T> member) {
		return (member.parseValueToString(this.context));
	}
	
	public String parseReferenceToString (MendixObjectReference reference) {
		return (reference.parseValueToString(this.context));
	}
}

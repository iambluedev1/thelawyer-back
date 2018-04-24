package fr.iambluedev.thelawyer.mysql.field;

import java.util.List;

import fr.vulkan.api.mysql.field.IField;
import fr.vulkan.api.mysql.field.IFields;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Fields implements IFields{

	private List<IField> fields;

	@Override
	public List<IField> getFields() {
		return this.fields;
	}
}

<?xml version="1.0" encoding="UTF-8"?>
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron">
  <sch:ns prefix="f" uri="http://hl7.org/fhir"/>
  <sch:pattern>
    <sch:title>Profile</sch:title>
    <sch:rule context="/f:Profile/f:resource">
      <sch:assert test="exists(f:profile) != exists(f:element)">Provide either a profile reference or constraints on the resource elements (but not both)</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Profile/f:resource/f:element">
      <sch:assert test="not(exists(f:bundled)) or exists(f:definition/f:type/f:code[starts-with(., 'Resource(')])">Bundled may only be specified if one of the allowed types for the element is a resource</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Profile/f:resource/f:element/f:definition">
      <sch:assert test="not(exists(f:nameReference) and exists(f:*[starts-with(local-name(.), 'value')]))">Either a name or a fixed value (but not both) is permitted</sch:assert>
      <sch:assert test="not(exists(f:*[starts-with(local-name(.), 'value')])) or (count(f:type)=1 and f:type/f:code[substring(.,1,1)=lower-case(substring(.,1,1))])">Value may only be specified if the type consists of a single repetition that has a type corresponding to one of the primitive data types.</sch:assert>
      <sch:assert test="not(exists(f:binding)) or f:type/f:code=('code','Coding','CodeableConcept')">Binding can only be present for coded elements</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Profile/f:resource/f:element/f:definition/f:max">
      <sch:assert test=".='*' or (normalize-space(.)!='' and normalize-space(translate(., '0123456789',''))='')">Max must be a number or "*"</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Profile/f:resource/f:element/f:definition/f:type">
      <sch:assert test="not(exists(f:profile)) or starts-with(f:code, 'Resource(')">Profile is only allowed if code is a resource</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Profile/f:extensionDefn/f:code">
      <sch:assert test="count(ancestor::f:Profile/f:extensionDefn/f:code[.=current()])=1">Codes must be unique in the context of a profile</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Profile/f:binding">
      <sch:assert test="(f:type=('reference', 'valueset')) = exists(f:reference)">reference must be present and may only be present if binding type is "reference" or "valueset"</sch:assert>
      <sch:assert test="(f:type='codelist') = exists(f:concept)">concepts must be present and may only be present if binding type is "codelist"</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Profile/f:binding/f:name">
      <sch:assert test="count(ancestor::f:Profile/f:binding/f:name[.=current()])=1">Binding name must be unique in the context of a profile</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Profile/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Profile/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
  </sch:pattern>
</sch:schema>

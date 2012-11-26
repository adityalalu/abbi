<?xml version="1.0" encoding="UTF-8"?>
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron">
  <sch:ns prefix="f" uri="http://hl7.org/fhir"/>
  <sch:pattern>
    <sch:title>ValueSet</sch:title>
    <sch:rule context="/f:ValueSet">
      <sch:assert test="count(f:include)!=1 or exists(f:include) or exists(f:exclude)">A value set with only one import must also have an include and/or an exclude</sch:assert>
      <sch:assert test="exists(f:include) or exists(f:import)">A value set must have an include or an import</sch:assert>
    </sch:rule>
    <sch:rule context="/f:ValueSet/f:include">
      <sch:assert test="count(f:code|f:filter)!=0">A selection criteria must have at least one code or filter</sch:assert>
    </sch:rule>
    <sch:rule context="/f:ValueSet/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/f:ValueSet/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
  </sch:pattern>
</sch:schema>

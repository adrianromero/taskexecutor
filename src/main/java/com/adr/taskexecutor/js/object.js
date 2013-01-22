//
//    Simple-lists is a personal data list manager.
//    Copyright (C) 2012 The Simple Lists Team.
//    All rights reserved.
//

/*jshint forin:false, noarg:true, noempty:true, latedef:true, eqeqeq:true, bitwise:false, undef:true, curly:true, indent:4, maxerr:50, laxbreak:true */
/*global ND: false */

(function () {

    // Extend a given object with all the properties in passed-in object.
    var extend = function(obj, source) {
        for (var prop in source) {
            obj[prop] = source[prop];
        }
        return obj;
    };

  // Copied from Backbone JS
  // Shared empty constructor function to aid in prototype-chain creation.
  var ctor = function(){};
  // Copied from Backbone JS
  // Helper function to correctly set up the prototype chain, for subclasses.
  // Similar to `goog.inherits`, but uses a hash of prototype properties and
  // class properties to be extended.
  var inherits = function(parent, protoProps, staticProps) {
    var child;

    // The constructor function for the new subclass is either defined by you
    // (the "constructor" property in your `extend` definition), or defaulted
    // by us to simply call the parent's constructor.
    if (protoProps && protoProps.hasOwnProperty('constructor')) {
      child = protoProps.constructor;
    } else {
      child = function(){ parent.apply(this, arguments); };
    }

    // Inherit class (static) properties from parent.
    extend(child, parent);

    // Set the prototype chain to inherit from `parent`, without calling
    // `parent`'s constructor function.
    ctor.prototype = parent.prototype;
    child.prototype = new ctor();

    // Add prototype properties (instance properties) to the subclass,
    // if supplied.
    if (protoProps) extend(child.prototype, protoProps);

    // Add static properties to the constructor function, if supplied.
    if (staticProps) extend(child, staticProps);

    // Correctly set child's `prototype.constructor`.
    child.prototype.constructor = child;

    // Set a convenience property in case the parent's prototype is needed later.
    child.__super__ = parent.prototype;

    return child;
  };

    var Object = function () {};
    // copied from Backbone js
    Object.extend = function (protoProps, classProps) {
        var child = inherits(this, protoProps, classProps);
        child.extend = this.extend;
        return child;
    };

    tools.Object = Object;
    tools.extend = extend;
}());

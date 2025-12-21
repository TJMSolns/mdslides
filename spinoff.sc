#!/usr/bin/env -S mill -i

import $file.`mill-spinoff-plugin`.build
import mill._
import mill.define.Module

/**
 * Spinoff script for extracting mdslides to production repository.
 *
 * Usage:
 *   mill -i spinoff.sc spinoffCmd.spinoffExecute --org tmoores-retisio
 *
 * This extracts mdslides from copilot-training to a standalone GitHub repository
 * at https://github.com/tmoores-retisio/mdslides
 */

// Re-export the spinoff plugin
object spinoffCmd extends build.millSpinoffPlugin.SpinoffModule

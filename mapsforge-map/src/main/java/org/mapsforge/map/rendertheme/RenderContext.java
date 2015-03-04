/*
 * Copyright 2015 Ludwig M Brinckmann
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mapsforge.map.rendertheme;

import org.mapsforge.core.mapelements.MapElementContainer;
import org.mapsforge.map.layer.renderer.RendererJob;
import org.mapsforge.map.layer.renderer.ShapePaintContainer;
import org.mapsforge.map.rendertheme.rule.RenderTheme;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * A RenderContext contains all the information and data to render a map area, it is passed between
 * calls in order to avoid local data stored in the DatabaseRenderer.
 *
 */
public class RenderContext {

	private static final byte LAYERS = 11;

	private static final double STROKE_INCREASE = 1.5;
	private static final byte STROKE_MIN_ZOOM_LEVEL = 12;

	// Configuration that drives the rendering
	public final RenderTheme renderTheme;
	public final RendererJob rendererJob;

	// Data generated for the rendering process
	private List<List<ShapePaintContainer>> drawingLayers;
	public final List<MapElementContainer> labels;
	public final List<List<List<ShapePaintContainer>>> ways;


	public RenderContext(RenderTheme renderTheme, RendererJob rendererJob) {
		this.rendererJob = rendererJob;
		this.labels = new LinkedList<MapElementContainer>();
		this.renderTheme = renderTheme;
		this.ways = createWayLists();
		setScaleStrokeWidth(rendererJob.tile.zoomLevel);
		renderTheme.scaleTextSize(rendererJob.textScale);
	}

	public void setDrawingLayers(byte layer) {
		if (layer < 0) {
			layer =  0;
		} else if (layer >= RenderContext.LAYERS) {
			layer = RenderContext.LAYERS - 1;
		}
		this.drawingLayers = ways.get(layer);
	}

	public void addToCurrentDrawingLayer(int level, ShapePaintContainer element) {
		this.drawingLayers.get(level).add(element);
	}

	private List<List<List<ShapePaintContainer>>> createWayLists() {
		List<List<List<ShapePaintContainer>>> result = new ArrayList<List<List<ShapePaintContainer>>>(LAYERS);
		int levels = this.renderTheme.getLevels();

		for (byte i = LAYERS - 1; i >= 0; --i) {
			List<List<ShapePaintContainer>> innerWayList = new ArrayList<List<ShapePaintContainer>>(levels);
			for (int j = levels - 1; j >= 0; --j) {
				innerWayList.add(new ArrayList<ShapePaintContainer>(0));
			}
			result.add(innerWayList);
		}
		return result;
	}

	/**
	 * Sets the scale stroke factor for the given zoom level.
	 *
	 * @param zoomLevel
	 *            the zoom level for which the scale stroke factor should be set.
	 */
	private void setScaleStrokeWidth(byte zoomLevel) {
		int zoomLevelDiff = Math.max(zoomLevel - STROKE_MIN_ZOOM_LEVEL, 0);
		renderTheme.scaleStrokeWidth((float) Math.pow(STROKE_INCREASE, zoomLevelDiff));
	}

}

import React from 'react';
import Svg, { Defs, LinearGradient, Stop, Rect } from 'react-native-svg';

interface FadeEdgeProps {
  height?: number;
  color?: string;
  /** 'bottom' = solid at the bottom (content fades upward into it);
   *  'top'    = solid at the top (content fades downward into it). */
  edge: 'top' | 'bottom';
}

let uid = 0;

export const FadeEdge: React.FC<FadeEdgeProps> = ({ height = 24, color = '#FFFFFF', edge }) => {
  const id = React.useMemo(() => `fade_${uid++}`, []);
  const solidTop = edge === 'top';
  return (
    <Svg width="100%" height={height} style={{ pointerEvents: 'none' }}>
      <Defs>
        <LinearGradient id={id} x1="0" y1="0" x2="0" y2="1">
          <Stop offset="0" stopColor={color} stopOpacity={solidTop ? 1 : 0} />
          <Stop offset="1" stopColor={color} stopOpacity={solidTop ? 0 : 1} />
        </LinearGradient>
      </Defs>
      <Rect x="0" y="0" width="100%" height={height} fill={`url(#${id})`} />
    </Svg>
  );
};
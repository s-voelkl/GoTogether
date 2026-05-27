import React from 'react';
import { TouchableOpacity, StyleSheet } from 'react-native';
import { colors, radius } from '../theme';
import { FilterIcon, CloseIcon } from './Icons';

interface FilterButtonProps {
  /** When true, shows the close X; otherwise the filter glyph. */
  open?: boolean;
  /** Optional handler. Omit on screens that aren't filterable yet. */
  onPress?: () => void;
}

/**
 * The yellow-blue pill in the header's right slot. Visually identical on
 * every screen so it acts as a layout anchor; behavior is controlled by
 * the parent (no handler = decorative).
 */
export const FilterButton: React.FC<FilterButtonProps> = ({ open = false, onPress }) => (
  <TouchableOpacity
    style={styles.btn}
    onPress={onPress}
    activeOpacity={0.85}
    disabled={!onPress}
  >
    {open ? <CloseIcon size={26} color={colors.black} /> : <FilterIcon size={26} color={colors.black} />}
  </TouchableOpacity>
);

const styles = StyleSheet.create({
  btn: {
    width: 88,
    height: 48,
    backgroundColor: colors.blue,
    borderRadius: radius.full,
    borderWidth: 2,
    borderColor: colors.black,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
